# ============================================
# Monitoring Module Variables
# ============================================

variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "backend_url" {
  description = "Backend service URL"
  type        = string
}

variable "frontend_url" {
  description = "Frontend service URL"
  type        = string
}

variable "backend_service_name" {
  description = "Backend Cloud Run service name"
  type        = string
}

variable "frontend_service_name" {
  description = "Frontend Cloud Run service name"
  type        = string
}

variable "backend_health_path" {
  description = "Backend health check path"
  type        = string
  default     = "/actuator/health"
}

variable "frontend_health_path" {
  description = "Frontend health check path"
  type        = string
  default     = "/health"
}

variable "alert_emails" {
  description = "Email addresses for alerts"
  type        = list(string)
  default     = []
}

variable "slack_webhook_url" {
  description = "Slack webhook URL for alerts"
  type        = string
  default     = ""
  sensitive   = true
}

variable "slack_channel" {
  description = "Slack channel name"
  type        = string
  default     = "#alerts"
}

variable "error_rate_threshold" {
  description = "Error rate threshold percentage"
  type        = number
  default     = 5
}

variable "latency_threshold_ms" {
  description = "P95 latency threshold in milliseconds"
  type        = number
  default     = 2000
}

variable "enable_database_alerts" {
  description = "Enable database monitoring alerts"
  type        = bool
  default     = true
}

variable "database_cpu_threshold" {
  description = "Database CPU threshold percentage"
  type        = number
  default     = 80
}
