# ============================================
# Database Module
# ============================================
# Creates Cloud SQL PostgreSQL instance with HA configuration

resource "google_sql_database_instance" "main" {
  name                = "${var.name_prefix}-postgres-${var.random_suffix}"
  database_version    = var.db_version
  region              = var.region
  deletion_protection = var.deletion_protection

  settings {
    tier                  = var.db_tier
    availability_type     = var.availability_type
    disk_size             = var.disk_size
    disk_type             = "PD_SSD"
    disk_autoresize       = var.disk_autoresize
    disk_autoresize_limit = var.disk_autoresize_limit

    # Private IP configuration (no public IP)
    ip_configuration {
      ipv4_enabled                                  = false
      private_network                               = var.network_id
      enable_private_path_for_google_cloud_services = true
    }

    # Backup configuration
    dynamic "backup_configuration" {
      for_each = var.backup_enabled ? [1] : []
      content {
        enabled                        = true
        start_time                     = var.backup_start_time
        point_in_time_recovery_enabled = var.point_in_time_recovery
        location                       = var.region

        backup_retention_settings {
          retained_backups = var.backup_retention_days
          retention_unit   = "COUNT"
        }
      }
    }

    # Maintenance window
    maintenance_window {
      day          = var.maintenance_window.day
      hour         = var.maintenance_window.hour
      update_track = var.maintenance_window.update_track
    }

    # Query Insights
    dynamic "insights_config" {
      for_each = var.insights_enabled ? [1] : []
      content {
        query_insights_enabled  = true
        query_string_length     = 1024
        record_application_tags = true
        record_client_address   = true
      }
    }

    # Database flags for production
    database_flags {
      name  = "max_connections"
      value = tostring(var.max_connections)
    }

    database_flags {
      name  = "log_checkpoints"
      value = "on"
    }

    database_flags {
      name  = "log_connections"
      value = "on"
    }

    database_flags {
      name  = "log_disconnections"
      value = "on"
    }

    dynamic "database_flags" {
      for_each = var.log_min_duration_statement != null ? [1] : []
      content {
        name  = "log_min_duration_statement"
        value = tostring(var.log_min_duration_statement)
      }
    }

    user_labels = var.labels
  }

  depends_on = [var.private_vpc_connection]
}

# Application Database
resource "google_sql_database" "main" {
  name     = var.db_name
  instance = google_sql_database_instance.main.name

  # Use ABANDON to avoid "database in use" errors during destroy
  # The database will be deleted when the instance is deleted
  deletion_policy = "ABANDON"
}

# Application Database User
resource "google_sql_user" "main" {
  name     = var.db_user
  instance = google_sql_database_instance.main.name
  password = var.db_password

  deletion_policy = "ABANDON"
}

# Read Replica (Optional)
resource "google_sql_database_instance" "replica" {
  count = var.enable_replica ? 1 : 0

  name                 = "${var.name_prefix}-postgres-replica-${var.random_suffix}"
  master_instance_name = google_sql_database_instance.main.name
  region               = var.region
  database_version     = var.db_version
  deletion_protection  = var.deletion_protection

  replica_configuration {
    failover_target = false
  }

  settings {
    tier              = var.db_tier
    availability_type = "ZONAL"
    disk_size         = var.disk_size
    disk_type         = "PD_SSD"
    disk_autoresize   = var.disk_autoresize

    ip_configuration {
      ipv4_enabled    = false
      private_network = var.network_id
    }

    database_flags {
      name  = "max_connections"
      value = tostring(var.max_connections)
    }

    user_labels = merge(var.labels, { role = "replica" })
  }
}
