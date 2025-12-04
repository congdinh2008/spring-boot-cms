# ============================================
# Load Balancer Module Outputs
# ============================================

output "global_ip" {
  description = "Global IP address"
  value       = google_compute_global_address.main.address
}

output "global_ip_name" {
  description = "Global IP address name"
  value       = google_compute_global_address.main.name
}

output "url_map_id" {
  description = "URL map ID"
  value       = google_compute_url_map.main.id
}

output "ssl_certificate_id" {
  description = "SSL certificate ID"
  value       = length(var.domains) > 0 ? google_compute_managed_ssl_certificate.main[0].id : null
}

output "backend_service_id" {
  description = "Backend service ID"
  value       = google_compute_backend_service.backend.id
}

output "frontend_service_id" {
  description = "Frontend service ID"
  value       = google_compute_backend_service.frontend.id
}
