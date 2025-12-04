# ============================================
# Production Environment Variables
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

variable "cost_center" {
  description = "Cost center for billing"
  type        = string
  default     = "production"
}

# ============================================
# Database Variables
# ============================================

variable "database_name" {
  description = "Database name"
  type        = string
  default     = "cms_prod"
}

variable "database_user" {
  description = "Database user"
  type        = string
  default     = "cms_prod_user"
}

variable "database_tier" {
  description = "Cloud SQL instance tier"
  type        = string
  default     = "db-custom-4-8192"
}

variable "database_disk_size" {
  description = "Database disk size in GB"
  type        = number
  default     = 50
}

variable "database_disk_limit" {
  description = "Maximum database disk size in GB"
  type        = number
  default     = 200
}

variable "enable_read_replica" {
  description = "Enable read replica"
  type        = bool
  default     = false
}

# ============================================
# Secrets
# ============================================

variable "jwt_secret" {
  description = "JWT signing secret"
  type        = string
  sensitive   = true
}

# ============================================
# Backend Configuration
# ============================================

variable "backend_image_tag" {
  description = "Backend Docker image tag"
  type        = string
}

variable "backend_cpu" {
  description = "Backend CPU allocation"
  type        = string
  default     = "2"
}

variable "backend_memory" {
  description = "Backend memory allocation"
  type        = string
  default     = "2Gi"
}

variable "backend_min_instances" {
  description = "Backend minimum instances"
  type        = number
  default     = 2
}

variable "backend_max_instances" {
  description = "Backend maximum instances"
  type        = number
  default     = 20
}

# ============================================
# Frontend Configuration
# ============================================

variable "frontend_image_tag" {
  description = "Frontend Docker image tag"
  type        = string
}

variable "frontend_cpu" {
  description = "Frontend CPU allocation"
  type        = string
  default     = "1"
}

variable "frontend_memory" {
  description = "Frontend memory allocation"
  type        = string
  default     = "512Mi"
}

variable "frontend_min_instances" {
  description = "Frontend minimum instances"
  type        = number
  default     = 2
}

variable "frontend_max_instances" {
  description = "Frontend maximum instances"
  type        = number
  default     = 20
}

# ============================================
# Security Configuration
# ============================================

variable "domains" {
  description = "Domains for SSL certificate"
  type        = list(string)
  default     = []
}

variable "cors_origins" {
  description = "CORS allowed origins"
  type        = string
}

variable "rate_limit_requests" {
  description = "Rate limit requests per interval for backend"
  type        = number
  default     = 100
}

variable "frontend_rate_limit_requests" {
  description = "Rate limit requests per interval for frontend"
  type        = number
  default     = 500
}

variable "blocked_countries" {
  description = "List of country codes to block"
  type        = list(string)
  default     = []
}

variable "allowed_ips" {
  description = "List of IPs to whitelist"
  type        = list(string)
  default     = []
}

variable "blocked_ips" {
  description = "List of IPs to blacklist"
  type        = list(string)
  default     = []
}

# ============================================
# Monitoring Configuration
# ============================================

variable "alert_emails" {
  description = "Email addresses for alerts"
  type        = list(string)
}

variable "slack_webhook_url" {
  description = "Slack webhook URL for alerts"
  type        = string
  default     = ""
  sensitive   = true
}

variable "slack_channel" {
  description = "Slack channel for alerts"
  type        = string
  default     = "#alerts"
}
