# ============================================
# Cloud Run Module Variables
# ============================================

variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
}

variable "service_account_email" {
  description = "Service account email for Cloud Run"
  type        = string
}

variable "vpc_connector_id" {
  description = "VPC Access Connector ID"
  type        = string
}

variable "backend_config" {
  description = "Backend service configuration"
  type = object({
    image                 = string
    cpu                   = string
    memory                = string
    min_instances         = number
    max_instances         = number
    timeout               = number
    cpu_idle              = bool
    health_path           = string
    liveness_path         = string
    startup_initial_delay = number
  })
  default = {
    image                 = null
    cpu                   = "1"
    memory                = "1Gi"
    min_instances         = 1
    max_instances         = 10
    timeout               = 300
    cpu_idle              = false
    health_path           = "/actuator/health"
    liveness_path         = "/actuator/health/liveness"
    startup_initial_delay = 10
  }
}

variable "frontend_config" {
  description = "Frontend service configuration"
  type = object({
    image         = string
    cpu           = string
    memory        = string
    min_instances = number
    max_instances = number
    cpu_idle      = bool
    health_path   = string
  })
  default = {
    image         = null
    cpu           = "1"
    memory        = "512Mi"
    min_instances = 1
    max_instances = 10
    cpu_idle      = true
    health_path   = "/health"
  }
}

variable "backend_env_vars" {
  description = "Environment variables for backend"
  type        = map(string)
  default     = {}
}

variable "backend_secret_env_vars" {
  description = "Secret environment variables for backend"
  type = map(object({
    secret_id = string
    version   = string
  }))
  default = {}
}

variable "frontend_env_vars" {
  description = "Environment variables for frontend"
  type        = map(string)
  default     = {}
}

variable "backend_public" {
  description = "Make backend publicly accessible"
  type        = bool
  default     = true
}

variable "frontend_public" {
  description = "Make frontend publicly accessible"
  type        = bool
  default     = true
}

variable "labels" {
  description = "Labels to apply to resources"
  type        = map(string)
  default     = {}
}
