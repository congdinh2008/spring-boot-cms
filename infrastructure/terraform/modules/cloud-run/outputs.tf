# ============================================
# Cloud Run Module Outputs
# ============================================

output "backend_url" {
  description = "Backend service URL"
  value       = google_cloud_run_v2_service.backend.uri
}

output "backend_name" {
  description = "Backend service name"
  value       = google_cloud_run_v2_service.backend.name
}

output "frontend_url" {
  description = "Frontend service URL"
  value       = google_cloud_run_v2_service.frontend.uri
}

output "frontend_name" {
  description = "Frontend service name"
  value       = google_cloud_run_v2_service.frontend.name
}

output "backend_service_id" {
  description = "Backend service ID"
  value       = google_cloud_run_v2_service.backend.id
}

output "frontend_service_id" {
  description = "Frontend service ID"
  value       = google_cloud_run_v2_service.frontend.id
}
