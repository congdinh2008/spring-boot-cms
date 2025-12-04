# ============================================
# Artifact Registry Module
# ============================================
# Creates container registry for Docker images

resource "google_artifact_registry_repository" "main" {
  location      = var.region
  repository_id = "${var.name_prefix}-images"
  format        = "DOCKER"
  description   = "Docker images for ${var.app_name} application"

  labels = var.labels

  # Cleanup policy for old images
  dynamic "cleanup_policies" {
    for_each = var.enable_cleanup_policies ? [1] : []
    content {
      id     = "keep-minimum-versions"
      action = "KEEP"

      most_recent_versions {
        keep_count = var.keep_image_count
      }
    }
  }

  dynamic "cleanup_policies" {
    for_each = var.enable_cleanup_policies ? [1] : []
    content {
      id     = "delete-old-untagged"
      action = "DELETE"

      condition {
        older_than = "${var.cleanup_older_than_days * 24 * 60 * 60}s"
        tag_state  = "UNTAGGED"
      }
    }
  }
}

# Allow specific members to pull images
resource "google_artifact_registry_repository_iam_member" "readers" {
  for_each   = toset(var.image_readers)
  location   = google_artifact_registry_repository.main.location
  repository = google_artifact_registry_repository.main.name
  role       = "roles/artifactregistry.reader"
  member     = each.value
}

# Allow specific members to push images
resource "google_artifact_registry_repository_iam_member" "writers" {
  for_each   = toset(var.image_writers)
  location   = google_artifact_registry_repository.main.location
  repository = google_artifact_registry_repository.main.name
  role       = "roles/artifactregistry.writer"
  member     = each.value
}
