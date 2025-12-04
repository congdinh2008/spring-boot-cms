# ============================================
# Monitoring Module Outputs
# ============================================

output "backend_uptime_check_id" {
  description = "Backend uptime check ID"
  value       = google_monitoring_uptime_check_config.backend.uptime_check_id
}

output "frontend_uptime_check_id" {
  description = "Frontend uptime check ID"
  value       = google_monitoring_uptime_check_config.frontend.uptime_check_id
}

output "dashboard_id" {
  description = "Monitoring dashboard ID"
  value       = google_monitoring_dashboard.main.id
}

output "error_logs_metric_name" {
  description = "Error logs metric name"
  value       = google_logging_metric.error_logs.name
}

output "notification_channel_ids" {
  description = "Notification channel IDs"
  value = concat(
    [for email in var.alert_emails : google_monitoring_notification_channel.email[email].id],
    var.slack_webhook_url != "" ? [google_monitoring_notification_channel.slack[0].id] : []
  )
}
