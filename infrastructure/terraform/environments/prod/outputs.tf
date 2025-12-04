# ============================================
# Production Environment Outputs
# ============================================

output "backend_url" {
  description = "Backend service URL"
  value       = module.cloud_run.backend_url
}

output "frontend_url" {
  description = "Frontend service URL"
  value       = module.cloud_run.frontend_url
}

output "load_balancer_ip" {
  description = "Load Balancer global IP"
  value       = module.load_balancer.global_ip
}

output "database_connection_name" {
  description = "Cloud SQL connection name"
  value       = module.database.instance_connection_name
}

output "database_private_ip" {
  description = "Cloud SQL private IP"
  value       = module.database.private_ip_address
  sensitive   = true
}

output "artifact_registry_url" {
  description = "Artifact Registry URL"
  value       = module.artifact_registry.repository_url
}

output "docker_push_command" {
  description = "Command to push Docker images"
  value       = module.artifact_registry.docker_push_command
}

output "monitoring_dashboard_id" {
  description = "Monitoring dashboard ID"
  value       = module.monitoring.dashboard_id
}

output "ssl_certificate_id" {
  description = "SSL certificate ID"
  value       = module.load_balancer.ssl_certificate_id
}

output "cicd_service_account" {
  description = "CI/CD service account email"
  value       = module.iam.cicd_service_account_email
}
