# ============================================
# Cloud Run Module
# ============================================
# Creates Cloud Run services for backend and frontend

# Backend Service
resource "google_cloud_run_v2_service" "backend" {
  name     = "${var.name_prefix}-backend"
  location = var.region

  template {
    service_account       = var.service_account_email
    execution_environment = "EXECUTION_ENVIRONMENT_GEN2"
    timeout               = "${var.backend_config.timeout}s"

    scaling {
      min_instance_count = var.backend_config.min_instances
      max_instance_count = var.backend_config.max_instances
    }

    vpc_access {
      connector = var.vpc_connector_id
      egress    = "PRIVATE_RANGES_ONLY"
    }

    containers {
      image = var.backend_config.image

      ports {
        container_port = 8080
      }

      resources {
        limits = {
          cpu    = var.backend_config.cpu
          memory = var.backend_config.memory
        }
        cpu_idle = var.backend_config.cpu_idle
      }

      # Environment variables
      dynamic "env" {
        for_each = var.backend_env_vars
        content {
          name  = env.key
          value = env.value
        }
      }

      # Secret environment variables
      dynamic "env" {
        for_each = var.backend_secret_env_vars
        content {
          name = env.key
          value_source {
            secret_key_ref {
              secret  = env.value.secret_id
              version = env.value.version
            }
          }
        }
      }

      startup_probe {
        http_get {
          path = var.backend_config.health_path
          port = 8080
        }
        initial_delay_seconds = var.backend_config.startup_initial_delay
        timeout_seconds       = 5
        period_seconds        = 10
        failure_threshold     = 10
      }

      liveness_probe {
        http_get {
          path = var.backend_config.liveness_path
          port = 8080
        }
        initial_delay_seconds = 30
        timeout_seconds       = 5
        period_seconds        = 30
        failure_threshold     = 3
      }
    }

    labels = var.labels
  }

  traffic {
    type    = "TRAFFIC_TARGET_ALLOCATION_TYPE_LATEST"
    percent = 100
  }
}

# Frontend Service
resource "google_cloud_run_v2_service" "frontend" {
  name     = "${var.name_prefix}-frontend"
  location = var.region

  template {
    service_account       = var.service_account_email
    execution_environment = "EXECUTION_ENVIRONMENT_GEN2"

    scaling {
      min_instance_count = var.frontend_config.min_instances
      max_instance_count = var.frontend_config.max_instances
    }

    containers {
      image = var.frontend_config.image

      ports {
        container_port = 80
      }

      resources {
        limits = {
          cpu    = var.frontend_config.cpu
          memory = var.frontend_config.memory
        }
        cpu_idle = var.frontend_config.cpu_idle
      }

      # Environment variables
      dynamic "env" {
        for_each = merge(var.frontend_env_vars, {
          BACKEND_URL = google_cloud_run_v2_service.backend.uri
        })
        content {
          name  = env.key
          value = env.value
        }
      }

      startup_probe {
        http_get {
          path = var.frontend_config.health_path
          port = 80
        }
        initial_delay_seconds = 2
        timeout_seconds       = 3
        period_seconds        = 5
        failure_threshold     = 5
      }

      liveness_probe {
        http_get {
          path = var.frontend_config.health_path
          port = 80
        }
        initial_delay_seconds = 5
        timeout_seconds       = 3
        period_seconds        = 30
        failure_threshold     = 3
      }
    }

    labels = var.labels
  }

  traffic {
    type    = "TRAFFIC_TARGET_ALLOCATION_TYPE_LATEST"
    percent = 100
  }

  depends_on = [google_cloud_run_v2_service.backend]
}

# IAM - Public Access
resource "google_cloud_run_v2_service_iam_member" "backend_public" {
  count    = var.backend_public ? 1 : 0
  project  = var.project_id
  location = var.region
  name     = google_cloud_run_v2_service.backend.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}

resource "google_cloud_run_v2_service_iam_member" "frontend_public" {
  count    = var.frontend_public ? 1 : 0
  project  = var.project_id
  location = var.region
  name     = google_cloud_run_v2_service.frontend.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
