# ============================================
# Production Environment Configuration
# ============================================
# Full HA resources for production

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.0"
    }
  }

  # Remote state - REQUIRED for production
  # backend "gcs" {
  #   bucket = "your-terraform-state-bucket"
  #   prefix = "cms/prod"
  # }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

provider "google-beta" {
  project = var.project_id
  region  = var.region
}

# Random suffix for unique naming
resource "random_id" "suffix" {
  byte_length = 4
}

# Local variables
locals {
  environment = "prod"
  name_prefix = "${var.app_name}-${local.environment}"

  labels = {
    app         = var.app_name
    environment = local.environment
    managed_by  = "terraform"
    cost_center = var.cost_center
  }
}

# Enable required APIs
resource "google_project_service" "apis" {
  for_each = toset([
    "run.googleapis.com",
    "sql-component.googleapis.com",
    "sqladmin.googleapis.com",
    "compute.googleapis.com",
    "vpcaccess.googleapis.com",
    "secretmanager.googleapis.com",
    "artifactregistry.googleapis.com",
    "cloudresourcemanager.googleapis.com",
    "iam.googleapis.com",
    "monitoring.googleapis.com",
    "logging.googleapis.com",
    "servicenetworking.googleapis.com",
    "cloudarmor.googleapis.com",
    "certificatemanager.googleapis.com",
  ])

  service            = each.key
  disable_on_destroy = false
}

# Generate database password
resource "random_password" "db_password" {
  length           = 48
  special          = true
  override_special = "!@#$%^&*"
}

# Network Module
module "network" {
  source = "../../modules/network"

  name_prefix = local.name_prefix
  region      = var.region

  vpc_cidr               = "10.2.0.0/16"
  vpc_connector_cidr     = "10.10.0.0/28"
  private_ip_cidr_prefix = 16

  # Production: More capacity
  vpc_connector_min_instances = 2
  vpc_connector_max_instances = 10

  enable_flow_logs = true
  enable_nat_logs  = true

  depends_on = [google_project_service.apis]
}

# Database Module
module "database" {
  source = "../../modules/database"

  name_prefix   = local.name_prefix
  region        = var.region
  random_suffix = random_id.suffix.hex

  # Network
  network_id             = module.network.network_id
  private_vpc_connection = module.network.private_vpc_connection

  # Database config
  db_name     = var.database_name
  db_user     = var.database_user
  db_password = random_password.db_password.result

  # Production: High availability
  db_tier                = var.database_tier
  disk_size              = var.database_disk_size
  disk_autoresize        = true
  disk_autoresize_limit  = var.database_disk_limit
  availability_type      = "REGIONAL" # Multi-zone HA
  backup_enabled         = true
  backup_retention_days  = 30
  point_in_time_recovery = true
  deletion_protection    = true
  enable_replica         = var.enable_read_replica
  insights_enabled       = true
  max_connections        = 400

  # Maintenance window: Sunday 3 AM
  maintenance_window = {
    day          = 7
    hour         = 3
    update_track = "stable"
  }

  labels = local.labels

  depends_on = [module.network]
}

# Secrets Module
module "secrets" {
  source = "../../modules/secrets"

  name_prefix = local.name_prefix

  db_password = random_password.db_password.result
  jwt_secret  = var.jwt_secret

  labels = local.labels

  depends_on = [google_project_service.apis]
}

# IAM Module
module "iam" {
  source = "../../modules/iam"

  project_id  = var.project_id
  name_prefix = local.name_prefix
  app_name    = var.app_name

  enable_error_reporting = true
  create_cicd_sa         = true

  depends_on = [module.secrets]
}

# Grant secret access to Cloud Run service account
resource "google_secret_manager_secret_iam_member" "db_password_access" {
  secret_id = module.secrets.db_password_secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = module.iam.cloud_run_service_account_member

  depends_on = [module.iam, module.secrets]
}

resource "google_secret_manager_secret_iam_member" "jwt_secret_access" {
  secret_id = module.secrets.jwt_secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = module.iam.cloud_run_service_account_member

  depends_on = [module.iam, module.secrets]
}

# Artifact Registry Module
module "artifact_registry" {
  source = "../../modules/artifact-registry"

  name_prefix = local.name_prefix
  region      = var.region
  app_name    = var.app_name

  enable_cleanup_policies = true
  keep_image_count        = 20
  cleanup_older_than_days = 90

  labels = local.labels

  depends_on = [google_project_service.apis]
}

# Cloud Run Module
module "cloud_run" {
  source = "../../modules/cloud-run"

  project_id            = var.project_id
  name_prefix           = local.name_prefix
  region                = var.region
  service_account_email = module.iam.cloud_run_service_account_email
  vpc_connector_id      = module.network.vpc_connector_id

  # Backend Config - Production
  backend_config = {
    image                 = "${var.region}-docker.pkg.dev/${var.project_id}/${module.artifact_registry.repository_id}/backend:${var.backend_image_tag}"
    cpu                   = var.backend_cpu
    memory                = var.backend_memory
    min_instances         = var.backend_min_instances
    max_instances         = var.backend_max_instances
    timeout               = 300
    cpu_idle              = false # Always-on for production
    health_path           = "/actuator/health"
    liveness_path         = "/actuator/health/liveness"
    startup_initial_delay = 15
  }

  # Frontend Config - Production
  frontend_config = {
    image         = "${var.region}-docker.pkg.dev/${var.project_id}/${module.artifact_registry.repository_id}/frontend:${var.frontend_image_tag}"
    cpu           = var.frontend_cpu
    memory        = var.frontend_memory
    min_instances = var.frontend_min_instances
    max_instances = var.frontend_max_instances
    cpu_idle      = false
    health_path   = "/health"
  }

  backend_env_vars = {
    SPRING_PROFILES_ACTIVE     = "prod"
    SPRING_DATASOURCE_URL      = "jdbc:postgresql://${module.database.private_ip_address}:5432/${var.database_name}"
    SPRING_DATASOURCE_USERNAME = var.database_user
    CORS_ORIGINS               = var.cors_origins
    LOG_LEVEL                  = "WARN"
    JAVA_OPTS                  = "-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"
  }

  backend_secret_env_vars = {
    SPRING_DATASOURCE_PASSWORD = {
      secret_id = module.secrets.db_password_secret_id
      version   = "latest"
    }
    JWT_SECRET = {
      secret_id = module.secrets.jwt_secret_id
      version   = "latest"
    }
  }

  frontend_env_vars = {
    VITE_API_URL = ""
  }

  backend_public  = true
  frontend_public = true
  labels          = local.labels

  depends_on = [
    module.iam,
    module.database,
    module.secrets,
    google_secret_manager_secret_iam_member.db_password_access,
    google_secret_manager_secret_iam_member.jwt_secret_access,
  ]
}

# Security Module (Cloud Armor)
module "security" {
  source = "../../modules/security"

  name_prefix = local.name_prefix

  enable_rate_limiting         = true
  rate_limit_requests          = var.rate_limit_requests
  frontend_rate_limit_requests = var.frontend_rate_limit_requests
  rate_limit_interval          = 60
  ban_duration                 = 600

  enable_owasp_rules     = true
  enable_ddos_protection = true

  blocked_countries = var.blocked_countries
  allowed_ips       = var.allowed_ips
  blocked_ips       = var.blocked_ips

  depends_on = [module.cloud_run]
}

# Load Balancer Module
module "load_balancer" {
  source = "../../modules/load-balancer"

  name_prefix = local.name_prefix
  region      = var.region

  domains               = var.domains
  backend_service_name  = module.cloud_run.backend_name
  frontend_service_name = module.cloud_run.frontend_name

  backend_security_policy  = module.security.backend_policy_self_link
  frontend_security_policy = module.security.frontend_policy_self_link

  enable_cdn      = true
  cdn_default_ttl = 3600
  cdn_max_ttl     = 86400
  cdn_client_ttl  = 3600

  log_sample_rate      = 1.0
  enable_http_redirect = true

  depends_on = [module.cloud_run, module.security]
}

# Monitoring Module
module "monitoring" {
  source = "../../modules/monitoring"

  project_id  = var.project_id
  name_prefix = local.name_prefix

  backend_url           = module.cloud_run.backend_url
  frontend_url          = module.cloud_run.frontend_url
  backend_service_name  = module.cloud_run.backend_name
  frontend_service_name = module.cloud_run.frontend_name

  backend_health_path  = "/actuator/health"
  frontend_health_path = "/health"

  alert_emails      = var.alert_emails
  slack_webhook_url = var.slack_webhook_url
  slack_channel     = var.slack_channel

  error_rate_threshold = 5
  latency_threshold_ms = 2000

  enable_database_alerts = true
  database_cpu_threshold = 70

  depends_on = [module.cloud_run]
}
