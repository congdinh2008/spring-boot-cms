# ============================================
# IAM Module Outputs
# ============================================

output "cloud_run_service_account_email" {
  description = "Cloud Run service account email"
  value       = google_service_account.cloud_run.email
}

output "cloud_run_service_account_name" {
  description = "Cloud Run service account name"
  value       = google_service_account.cloud_run.name
}

output "cloud_run_service_account_member" {
  description = "Cloud Run service account as IAM member"
  value       = "serviceAccount:${google_service_account.cloud_run.email}"
}

output "cicd_service_account_email" {
  description = "CI/CD service account email"
  value       = var.create_cicd_sa ? google_service_account.cicd[0].email : null
}
