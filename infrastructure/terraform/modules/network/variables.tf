# ============================================
# Network Module Variables
# ============================================

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR range for the VPC subnet"
  type        = string
  default     = "10.0.0.0/20"
}

variable "vpc_connector_cidr" {
  description = "CIDR range for VPC Connector"
  type        = string
  default     = "10.8.0.0/28"
}

variable "private_ip_cidr_prefix" {
  description = "Prefix length for Cloud SQL private IP range"
  type        = number
  default     = 16
}

variable "vpc_connector_min_instances" {
  description = "Minimum instances for VPC connector"
  type        = number
  default     = 2
}

variable "vpc_connector_max_instances" {
  description = "Maximum instances for VPC connector"
  type        = number
  default     = 10
}

variable "enable_flow_logs" {
  description = "Enable VPC flow logs"
  type        = bool
  default     = true
}

variable "enable_nat_logs" {
  description = "Enable Cloud NAT logs"
  type        = bool
  default     = true
}
