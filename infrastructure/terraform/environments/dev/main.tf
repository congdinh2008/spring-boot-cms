# ============================================
# Development Environment Configuration
# ============================================
# Minimal resources for development/testing

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

  # Uncomment and configure for remote state
  # backend "gcs" {
  #   bucket = "your-terraform-state-bucket"
  #   prefix = "cms/dev"
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
  environment = "dev"
  name_prefix = "${var.app_name}-${local.environment}"

  labels = {
    app         = var.app_name
    environment = local.environment
    managed_by  = "terraform"
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
  ])

  service            = each.key
  disable_on_destroy = false
}

# Generate database password
resource "random_password" "db_password" {
  length           = 32
  special          = true
  override_special = "!@#$%^&*"
}

# Network Module
module "network" {
  source = "../../modules/network"

  name_prefix = local.name_prefix
  region      = var.region

  # Dev: Smaller subnet
  vpc_cidr               = "10.0.0.0/24"
  vpc_connector_cidr     = "10.8.0.0/28"
  private_ip_cidr_prefix = 20

  # Dev: Minimal VPC connector
  vpc_connector_min_instances = 2
  vpc_connector_max_instances = 3

  enable_flow_logs = false
  enable_nat_logs  = false

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

  # Dev: Minimal resources
  db_tier                = "db-f1-micro"
  disk_size              = 10
  disk_autoresize        = true
  disk_autoresize_limit  = 20
  availability_type      = "ZONAL" # No HA for dev
  backup_enabled         = false
  point_in_time_recovery = false
  deletion_protection    = false
  enable_replica         = false
  insights_enabled       = false

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

  enable_error_reporting = false
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

  # Dev: Keep fewer versions
  enable_cleanup_policies = true
  keep_image_count        = 3
  cleanup_older_than_days = 7

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

  # Backend Config - Minimal for dev
  backend_config = {
    image                 = "${var.region}-docker.pkg.dev/${var.project_id}/${module.artifact_registry.repository_id}/backend:${var.backend_image_tag}"
    cpu                   = "1"
    memory                = "512Mi"
    min_instances         = 0 # Scale to zero when not in use
    max_instances         = 2
    timeout               = 300
    cpu_idle              = true # Allow CPU to be throttled
    health_path           = "/actuator/health"
    liveness_path         = "/actuator/health/liveness"
    startup_initial_delay = 10
  }

  # Frontend Config - Minimal for dev (512Mi min for gen2)
  frontend_config = {
    image         = "${var.region}-docker.pkg.dev/${var.project_id}/${module.artifact_registry.repository_id}/frontend:${var.frontend_image_tag}"
    cpu           = "1"
    memory        = "512Mi"
    min_instances = 0
    max_instances = 2
    cpu_idle      = true
    health_path   = "/health"
  }

  backend_env_vars = {
    SPRING_PROFILES_ACTIVE     = "dev"
    # Spring datasource configuration
    SPRING_DATASOURCE_URL      = "jdbc:postgresql://${module.database.private_ip_address}:5432/${var.database_name}"
    SPRING_DATASOURCE_USERNAME = var.database_user
    # CORS origins for direct backend access (Swagger, local dev)
    CORS_ORIGINS               = "https://*.run.app,http://localhost:5173,http://localhost:3000"
    LOG_LEVEL                  = "DEBUG"
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
    VITE_API_URL = "" # Will use relative path
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
