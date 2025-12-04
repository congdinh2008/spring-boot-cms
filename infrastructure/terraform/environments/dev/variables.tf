# ============================================
# Development Environment Variables
# ============================================

variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
  default     = "asia-southeast1"
}

variable "app_name" {
  description = "Application name"
  type        = string
  default     = "cms"
}

variable "database_name" {
  description = "Database name"
  type        = string
  default     = "cms_dev"
}

variable "database_user" {
  description = "Database user"
  type        = string
  default     = "cms_dev_user"
}

variable "jwt_secret" {
  description = "JWT signing secret"
  type        = string
  sensitive   = true
}

variable "backend_image_tag" {
  description = "Backend Docker image tag"
  type        = string
  default     = "latest"
}

variable "frontend_image_tag" {
  description = "Frontend Docker image tag"
  type        = string
  default     = "latest"
}
