# ============================================
# Monitoring Module
# ============================================
# Creates monitoring, alerting, and dashboards

# Notification Channel - Email
resource "google_monitoring_notification_channel" "email" {
  for_each     = toset(var.alert_emails)
  display_name = "Email: ${each.value}"
  type         = "email"

  labels = {
    email_address = each.value
  }
}

# Notification Channel - Slack (optional)
resource "google_monitoring_notification_channel" "slack" {
  count        = var.slack_webhook_url != "" ? 1 : 0
  display_name = "Slack Channel"
  type         = "slack"

  labels = {
    channel_name = var.slack_channel
  }

  sensitive_labels {
    auth_token = var.slack_webhook_url
  }
}

# Uptime Check - Backend
resource "google_monitoring_uptime_check_config" "backend" {
  display_name = "${var.name_prefix}-backend-uptime"
  timeout      = "10s"
  period       = "60s"

  http_check {
    path           = var.backend_health_path
    port           = 443
    use_ssl        = true
    validate_ssl   = true
    request_method = "GET"

    accepted_response_status_codes {
      status_value = 200
    }
  }

  monitored_resource {
    type = "uptime_url"
    labels = {
      project_id = var.project_id
      host       = replace(var.backend_url, "https://", "")
    }
  }

  content_matchers {
    content = "UP"
    matcher = "CONTAINS_STRING"
  }

  checker_type = "STATIC_IP_CHECKERS"
}

# Uptime Check - Frontend
resource "google_monitoring_uptime_check_config" "frontend" {
  display_name = "${var.name_prefix}-frontend-uptime"
  timeout      = "10s"
  period       = "60s"

  http_check {
    path           = var.frontend_health_path
    port           = 443
    use_ssl        = true
    validate_ssl   = true
    request_method = "GET"

    accepted_response_status_codes {
      status_value = 200
    }
  }

  monitored_resource {
    type = "uptime_url"
    labels = {
      project_id = var.project_id
      host       = replace(var.frontend_url, "https://", "")
    }
  }

  checker_type = "STATIC_IP_CHECKERS"
}

# Alert Policy - Backend Down
resource "google_monitoring_alert_policy" "backend_uptime" {
  display_name = "${var.name_prefix} - Backend Service Down"
  combiner     = "OR"

  conditions {
    display_name = "Backend Uptime Check Failed"

    condition_threshold {
      filter          = "resource.type = \"uptime_url\" AND metric.type = \"monitoring.googleapis.com/uptime_check/check_passed\" AND metric.labels.check_id = \"${google_monitoring_uptime_check_config.backend.uptime_check_id}\""
      duration        = "60s"
      comparison      = "COMPARISON_LT"
      threshold_value = 1

      aggregations {
        alignment_period     = "60s"
        per_series_aligner   = "ALIGN_NEXT_OLDER"
        cross_series_reducer = "REDUCE_COUNT_FALSE"
        group_by_fields      = ["resource.label.project_id"]
      }

      trigger {
        count = 1
      }
    }
  }

  notification_channels = concat(
    [for email in var.alert_emails : google_monitoring_notification_channel.email[email].id],
    var.slack_webhook_url != "" ? [google_monitoring_notification_channel.slack[0].id] : []
  )

  alert_strategy {
    auto_close = "604800s"
  }

  documentation {
    content   = "Backend service is down. Check Cloud Run logs and service health."
    mime_type = "text/markdown"
  }
}

# Alert Policy - Frontend Down
resource "google_monitoring_alert_policy" "frontend_uptime" {
  display_name = "${var.name_prefix} - Frontend Service Down"
  combiner     = "OR"

  conditions {
    display_name = "Frontend Uptime Check Failed"

    condition_threshold {
      filter          = "resource.type = \"uptime_url\" AND metric.type = \"monitoring.googleapis.com/uptime_check/check_passed\" AND metric.labels.check_id = \"${google_monitoring_uptime_check_config.frontend.uptime_check_id}\""
      duration        = "60s"
      comparison      = "COMPARISON_LT"
      threshold_value = 1

      aggregations {
        alignment_period     = "60s"
        per_series_aligner   = "ALIGN_NEXT_OLDER"
        cross_series_reducer = "REDUCE_COUNT_FALSE"
        group_by_fields      = ["resource.label.project_id"]
      }

      trigger {
        count = 1
      }
    }
  }

  notification_channels = concat(
    [for email in var.alert_emails : google_monitoring_notification_channel.email[email].id],
    var.slack_webhook_url != "" ? [google_monitoring_notification_channel.slack[0].id] : []
  )

  alert_strategy {
    auto_close = "604800s"
  }

  documentation {
    content   = "Frontend service is down. Check Cloud Run logs and nginx configuration."
    mime_type = "text/markdown"
  }
}

# Alert Policy - High Error Rate
resource "google_monitoring_alert_policy" "high_error_rate" {
  display_name = "${var.name_prefix} - High Error Rate"
  combiner     = "OR"

  conditions {
    display_name = "Error Rate > ${var.error_rate_threshold}%"

    condition_threshold {
      filter          = "resource.type = \"cloud_run_revision\" AND metric.type = \"run.googleapis.com/request_count\" AND metric.labels.response_code_class != \"2xx\""
      duration        = "300s"
      comparison      = "COMPARISON_GT"
      threshold_value = var.error_rate_threshold

      aggregations {
        alignment_period     = "60s"
        per_series_aligner   = "ALIGN_RATE"
        cross_series_reducer = "REDUCE_SUM"
      }

      trigger {
        count = 1
      }
    }
  }

  notification_channels = concat(
    [for email in var.alert_emails : google_monitoring_notification_channel.email[email].id],
    var.slack_webhook_url != "" ? [google_monitoring_notification_channel.slack[0].id] : []
  )

  alert_strategy {
    auto_close = "604800s"
  }

  documentation {
    content   = "Error rate is above ${var.error_rate_threshold}%. Check application logs for errors."
    mime_type = "text/markdown"
  }
}

# Alert Policy - High Latency
resource "google_monitoring_alert_policy" "high_latency" {
  display_name = "${var.name_prefix} - High Latency"
  combiner     = "OR"

  conditions {
    display_name = "P95 Latency > ${var.latency_threshold_ms}ms"

    condition_threshold {
      filter          = "resource.type = \"cloud_run_revision\" AND metric.type = \"run.googleapis.com/request_latencies\""
      duration        = "300s"
      comparison      = "COMPARISON_GT"
      threshold_value = var.latency_threshold_ms

      aggregations {
        alignment_period     = "60s"
        per_series_aligner   = "ALIGN_PERCENTILE_95"
        cross_series_reducer = "REDUCE_MAX"
      }

      trigger {
        count = 1
      }
    }
  }

  notification_channels = concat(
    [for email in var.alert_emails : google_monitoring_notification_channel.email[email].id],
    var.slack_webhook_url != "" ? [google_monitoring_notification_channel.slack[0].id] : []
  )

  alert_strategy {
    auto_close = "604800s"
  }

  documentation {
    content   = "P95 latency is above ${var.latency_threshold_ms}ms. Check for performance issues."
    mime_type = "text/markdown"
  }
}

# Alert Policy - Database CPU
resource "google_monitoring_alert_policy" "database_cpu" {
  count        = var.enable_database_alerts ? 1 : 0
  display_name = "${var.name_prefix} - Database High CPU"
  combiner     = "OR"

  conditions {
    display_name = "Database CPU > ${var.database_cpu_threshold}%"

    condition_threshold {
      filter          = "resource.type = \"cloudsql_database\" AND metric.type = \"cloudsql.googleapis.com/database/cpu/utilization\""
      duration        = "300s"
      comparison      = "COMPARISON_GT"
      threshold_value = var.database_cpu_threshold / 100

      aggregations {
        alignment_period     = "60s"
        per_series_aligner   = "ALIGN_MEAN"
        cross_series_reducer = "REDUCE_MAX"
      }

      trigger {
        count = 1
      }
    }
  }

  notification_channels = concat(
    [for email in var.alert_emails : google_monitoring_notification_channel.email[email].id],
    var.slack_webhook_url != "" ? [google_monitoring_notification_channel.slack[0].id] : []
  )

  alert_strategy {
    auto_close = "604800s"
  }

  documentation {
    content   = "Database CPU utilization is above ${var.database_cpu_threshold}%. Consider scaling up."
    mime_type = "text/markdown"
  }
}

# Log-based Metric for Error Logs
resource "google_logging_metric" "error_logs" {
  name        = "${var.name_prefix}-error-logs"
  description = "Count of error logs from Cloud Run services"
  filter      = "resource.type=\"cloud_run_revision\" AND severity>=ERROR"

  metric_descriptor {
    metric_kind = "DELTA"
    value_type  = "INT64"
    unit        = "1"

    labels {
      key         = "service_name"
      value_type  = "STRING"
      description = "Cloud Run service name"
    }
  }

  label_extractors = {
    "service_name" = "EXTRACT(resource.labels.service_name)"
  }
}

# Dashboard
resource "google_monitoring_dashboard" "main" {
  dashboard_json = jsonencode({
    displayName = "${var.name_prefix} Dashboard"
    gridLayout = {
      columns = 2
      widgets = [
        {
          title = "Backend Request Count"
          xyChart = {
            dataSets = [{
              timeSeriesQuery = {
                timeSeriesFilter = {
                  filter = "resource.type = \"cloud_run_revision\" AND resource.labels.service_name = \"${var.backend_service_name}\" AND metric.type = \"run.googleapis.com/request_count\""
                  aggregation = {
                    alignmentPeriod    = "60s"
                    perSeriesAligner   = "ALIGN_RATE"
                    crossSeriesReducer = "REDUCE_SUM"
                  }
                }
              }
              plotType = "LINE"
            }]
          }
        },
        {
          title = "Backend Latency (p95)"
          xyChart = {
            dataSets = [{
              timeSeriesQuery = {
                timeSeriesFilter = {
                  filter = "resource.type = \"cloud_run_revision\" AND resource.labels.service_name = \"${var.backend_service_name}\" AND metric.type = \"run.googleapis.com/request_latencies\""
                  aggregation = {
                    alignmentPeriod    = "60s"
                    perSeriesAligner   = "ALIGN_PERCENTILE_95"
                    crossSeriesReducer = "REDUCE_MAX"
                  }
                }
              }
              plotType = "LINE"
            }]
          }
        },
        {
          title = "Frontend Request Count"
          xyChart = {
            dataSets = [{
              timeSeriesQuery = {
                timeSeriesFilter = {
                  filter = "resource.type = \"cloud_run_revision\" AND resource.labels.service_name = \"${var.frontend_service_name}\" AND metric.type = \"run.googleapis.com/request_count\""
                  aggregation = {
                    alignmentPeriod    = "60s"
                    perSeriesAligner   = "ALIGN_RATE"
                    crossSeriesReducer = "REDUCE_SUM"
                  }
                }
              }
              plotType = "LINE"
            }]
          }
        },
        {
          title = "Error Rate"
          xyChart = {
            dataSets = [{
              timeSeriesQuery = {
                timeSeriesFilter = {
                  filter = "resource.type = \"cloud_run_revision\" AND metric.type = \"run.googleapis.com/request_count\" AND metric.labels.response_code_class != \"2xx\""
                  aggregation = {
                    alignmentPeriod    = "60s"
                    perSeriesAligner   = "ALIGN_RATE"
                    crossSeriesReducer = "REDUCE_SUM"
                  }
                }
              }
              plotType = "LINE"
            }]
          }
        },
        {
          title = "Cloud SQL CPU Utilization"
          xyChart = {
            dataSets = [{
              timeSeriesQuery = {
                timeSeriesFilter = {
                  filter = "resource.type = \"cloudsql_database\" AND metric.type = \"cloudsql.googleapis.com/database/cpu/utilization\""
                  aggregation = {
                    alignmentPeriod    = "60s"
                    perSeriesAligner   = "ALIGN_MEAN"
                    crossSeriesReducer = "REDUCE_MAX"
                  }
                }
              }
              plotType = "LINE"
            }]
          }
        },
        {
          title = "Cloud SQL Memory Utilization"
          xyChart = {
            dataSets = [{
              timeSeriesQuery = {
                timeSeriesFilter = {
                  filter = "resource.type = \"cloudsql_database\" AND metric.type = \"cloudsql.googleapis.com/database/memory/utilization\""
                  aggregation = {
                    alignmentPeriod    = "60s"
                    perSeriesAligner   = "ALIGN_MEAN"
                    crossSeriesReducer = "REDUCE_MAX"
                  }
                }
              }
              plotType = "LINE"
            }]
          }
        }
      ]
    }
  })
}
