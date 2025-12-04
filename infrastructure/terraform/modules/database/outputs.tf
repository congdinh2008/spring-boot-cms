# ============================================
# Database Module Outputs
# ============================================

output "instance_name" {
  description = "Cloud SQL instance name"
  value       = google_sql_database_instance.main.name
}

output "instance_connection_name" {
  description = "Cloud SQL connection name"
  value       = google_sql_database_instance.main.connection_name
}

output "private_ip_address" {
  description = "Cloud SQL private IP address"
  value       = google_sql_database_instance.main.private_ip_address
  sensitive   = true
}

output "database_name" {
  description = "Database name"
  value       = google_sql_database.main.name
}

output "database_user" {
  description = "Database user"
  value       = google_sql_user.main.name
}

output "connection_string" {
  description = "JDBC connection string"
  value       = "jdbc:postgresql://${google_sql_database_instance.main.private_ip_address}:5432/${google_sql_database.main.name}?sslmode=require"
  sensitive   = true
}

output "replica_ip_address" {
  description = "Replica private IP address"
  value       = var.enable_replica ? google_sql_database_instance.replica[0].private_ip_address : null
  sensitive   = true
}
