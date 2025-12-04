# ============================================
# Load Balancer Module Variables
# ============================================

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "region" {
  description = "GCP region for NEGs"
  type        = string
}

variable "domains" {
  description = "Domains for SSL certificate"
  type        = list(string)
  default     = []
}

variable "backend_service_name" {
  description = "Backend Cloud Run service name"
  type        = string
}

variable "frontend_service_name" {
  description = "Frontend Cloud Run service name"
  type        = string
}

variable "backend_security_policy" {
  description = "Security policy for backend"
  type        = string
  default     = null
}

variable "frontend_security_policy" {
  description = "Security policy for frontend"
  type        = string
  default     = null
}

variable "enable_cdn" {
  description = "Enable Cloud CDN for frontend"
  type        = bool
  default     = true
}

variable "cdn_default_ttl" {
  description = "Default TTL for CDN cache in seconds"
  type        = number
  default     = 3600
}

variable "cdn_max_ttl" {
  description = "Maximum TTL for CDN cache in seconds"
  type        = number
  default     = 86400
}

variable "cdn_client_ttl" {
  description = "Client TTL for CDN cache in seconds"
  type        = number
  default     = 3600
}

variable "log_sample_rate" {
  description = "Log sampling rate (0.0 to 1.0)"
  type        = number
  default     = 1.0
}

variable "enable_http_redirect" {
  description = "Enable HTTP to HTTPS redirect"
  type        = bool
  default     = true
}
