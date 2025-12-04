# ============================================
# IAM Module Variables
# ============================================

variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "app_name" {
  description = "Application name"
  type        = string
}

variable "enable_error_reporting" {
  description = "Enable Error Reporting"
  type        = bool
  default     = true
}

variable "create_cicd_sa" {
  description = "Create CI/CD service account"
  type        = bool
  default     = true
}
