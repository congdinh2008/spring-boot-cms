# ============================================
# Artifact Registry Module Outputs
# ============================================

output "repository_id" {
  description = "Artifact Registry repository ID"
  value       = google_artifact_registry_repository.main.repository_id
}

output "repository_name" {
  description = "Artifact Registry repository name"
  value       = google_artifact_registry_repository.main.name
}

output "repository_url" {
  description = "Artifact Registry URL for Docker images"
  value       = "${var.region}-docker.pkg.dev/${google_artifact_registry_repository.main.project}/${google_artifact_registry_repository.main.repository_id}"
}

output "docker_push_command" {
  description = "Docker push command template"
  value       = "docker push ${var.region}-docker.pkg.dev/${google_artifact_registry_repository.main.project}/${google_artifact_registry_repository.main.repository_id}/IMAGE:TAG"
}
