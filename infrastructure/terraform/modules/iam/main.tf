# ============================================
# IAM Module
# ============================================
# Creates service accounts and IAM bindings

# Service Account for Cloud Run Services
resource "google_service_account" "cloud_run" {
  account_id   = "${var.name_prefix}-cloud-run"
  display_name = "${var.app_name} Cloud Run Service Account"
  description  = "Service account for ${var.app_name} Cloud Run services"
}

# Cloud Run Service Account - Cloud SQL Client
resource "google_project_iam_member" "cloud_run_sql" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.cloud_run.email}"
}

# Cloud Run Service Account - Cloud Trace Agent
resource "google_project_iam_member" "cloud_run_trace" {
  project = var.project_id
  role    = "roles/cloudtrace.agent"
  member  = "serviceAccount:${google_service_account.cloud_run.email}"
}

# Cloud Run Service Account - Log Writer
resource "google_project_iam_member" "cloud_run_logging" {
  project = var.project_id
  role    = "roles/logging.logWriter"
  member  = "serviceAccount:${google_service_account.cloud_run.email}"
}

# Cloud Run Service Account - Metric Writer
resource "google_project_iam_member" "cloud_run_monitoring" {
  project = var.project_id
  role    = "roles/monitoring.metricWriter"
  member  = "serviceAccount:${google_service_account.cloud_run.email}"
}

# Cloud Run Service Account - Error Reporting Writer
resource "google_project_iam_member" "cloud_run_error_reporting" {
  count   = var.enable_error_reporting ? 1 : 0
  project = var.project_id
  role    = "roles/errorreporting.writer"
  member  = "serviceAccount:${google_service_account.cloud_run.email}"
}

# ============================================
# Service Account for CI/CD
# ============================================

resource "google_service_account" "cicd" {
  count        = var.create_cicd_sa ? 1 : 0
  account_id   = "${var.name_prefix}-cicd"
  display_name = "${var.app_name} CI/CD Service Account"
  description  = "Service account for CI/CD pipelines"
}

# CI/CD - Cloud Run Developer
resource "google_project_iam_member" "cicd_cloud_run" {
  count   = var.create_cicd_sa ? 1 : 0
  project = var.project_id
  role    = "roles/run.developer"
  member  = "serviceAccount:${google_service_account.cicd[0].email}"
}

# CI/CD - Artifact Registry Writer
resource "google_project_iam_member" "cicd_artifact_registry" {
  count   = var.create_cicd_sa ? 1 : 0
  project = var.project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.cicd[0].email}"
}

# CI/CD - Service Account User
resource "google_service_account_iam_member" "cicd_use_cloud_run_sa" {
  count              = var.create_cicd_sa ? 1 : 0
  service_account_id = google_service_account.cloud_run.name
  role               = "roles/iam.serviceAccountUser"
  member             = "serviceAccount:${google_service_account.cicd[0].email}"
}
