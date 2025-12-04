# ============================================
# Security Module Variables
# ============================================

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "enable_rate_limiting" {
  description = "Enable rate limiting"
  type        = bool
  default     = true
}

variable "rate_limit_requests" {
  description = "Number of requests allowed per interval for backend"
  type        = number
  default     = 100
}

variable "frontend_rate_limit_requests" {
  description = "Number of requests allowed per interval for frontend"
  type        = number
  default     = 500
}

variable "rate_limit_interval" {
  description = "Rate limit interval in seconds"
  type        = number
  default     = 60
}

variable "ban_duration" {
  description = "Ban duration in seconds after exceeding rate limit"
  type        = number
  default     = 300
}

variable "enable_owasp_rules" {
  description = "Enable OWASP security rules"
  type        = bool
  default     = true
}

variable "enable_ddos_protection" {
  description = "Enable DDoS protection"
  type        = bool
  default     = true
}

variable "blocked_countries" {
  description = "List of country codes to block"
  type        = list(string)
  default     = []
}

variable "allowed_ips" {
  description = "List of IP addresses to whitelist"
  type        = list(string)
  default     = []
}

variable "blocked_ips" {
  description = "List of IP addresses to blacklist"
  type        = list(string)
  default     = []
}
