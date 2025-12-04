# ============================================
# Artifact Registry Module Variables
# ============================================

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
}

variable "app_name" {
  description = "Application name"
  type        = string
}

variable "enable_cleanup_policies" {
  description = "Enable cleanup policies for old images"
  type        = bool
  default     = true
}

variable "keep_image_count" {
  description = "Number of recent images to keep"
  type        = number
  default     = 10
}

variable "cleanup_older_than_days" {
  description = "Delete untagged images older than X days"
  type        = number
  default     = 30
}

variable "image_readers" {
  description = "List of members that can read images"
  type        = list(string)
  default     = []
}

variable "image_writers" {
  description = "List of members that can write images"
  type        = list(string)
  default     = []
}

variable "labels" {
  description = "Labels to apply to resources"
  type        = map(string)
  default     = {}
}
