# ============================================
# Security Module Outputs
# ============================================

output "backend_policy_id" {
  description = "Backend security policy ID"
  value       = google_compute_security_policy.backend.id
}

output "backend_policy_self_link" {
  description = "Backend security policy self link"
  value       = google_compute_security_policy.backend.self_link
}

output "frontend_policy_id" {
  description = "Frontend security policy ID"
  value       = google_compute_security_policy.frontend.id
}

output "frontend_policy_self_link" {
  description = "Frontend security policy self link"
  value       = google_compute_security_policy.frontend.self_link
}
