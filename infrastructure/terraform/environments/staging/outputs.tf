# ============================================
# Staging Environment Outputs
# ============================================

output "backend_url" {
  description = "Backend service URL"
  value       = module.cloud_run.backend_url
}

output "frontend_url" {
  description = "Frontend service URL"
  value       = module.cloud_run.frontend_url
}

output "database_connection_name" {
  description = "Cloud SQL connection name"
  value       = module.database.instance_connection_name
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
