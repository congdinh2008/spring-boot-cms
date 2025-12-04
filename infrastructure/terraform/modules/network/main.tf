# ============================================
# Network Module
# ============================================
# Creates VPC, subnets, firewall rules, NAT, and VPC connector

# VPC Network
resource "google_compute_network" "main" {
  name                            = "${var.name_prefix}-network"
  auto_create_subnetworks         = false
  routing_mode                    = "REGIONAL"
  delete_default_routes_on_create = false
}

# Main Subnet
resource "google_compute_subnetwork" "main" {
  name                     = "${var.name_prefix}-subnet"
  ip_cidr_range            = var.vpc_cidr
  network                  = google_compute_network.main.id
  region                   = var.region
  private_ip_google_access = true

  dynamic "log_config" {
    for_each = var.enable_flow_logs ? [1] : []
    content {
      aggregation_interval = "INTERVAL_5_SEC"
      flow_sampling        = 0.5
      metadata             = "INCLUDE_ALL_METADATA"
    }
  }
}

# Private IP range for Cloud SQL (VPC Peering)
resource "google_compute_global_address" "private_ip" {
  name          = "${var.name_prefix}-private-ip"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = var.private_ip_cidr_prefix
  network       = google_compute_network.main.id
}

# VPC Peering for Cloud SQL Private Connection
resource "google_service_networking_connection" "private_vpc_connection" {
  network                 = google_compute_network.main.id
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip.name]
}

# VPC Access Connector for Cloud Run
resource "google_vpc_access_connector" "connector" {
  name          = "${var.name_prefix}-connector"
  region        = var.region
  ip_cidr_range = var.vpc_connector_cidr
  network       = google_compute_network.main.name

  min_instances = var.vpc_connector_min_instances
  max_instances = var.vpc_connector_max_instances
}

# ============================================
# Firewall Rules
# ============================================

# Allow internal traffic within VPC
resource "google_compute_firewall" "allow_internal" {
  name    = "${var.name_prefix}-allow-internal"
  network = google_compute_network.main.name

  allow {
    protocol = "tcp"
    ports    = ["0-65535"]
  }

  allow {
    protocol = "udp"
    ports    = ["0-65535"]
  }

  allow {
    protocol = "icmp"
  }

  source_ranges = [var.vpc_cidr, var.vpc_connector_cidr]
  priority      = 1000
}

# Allow health checks from Google Cloud
resource "google_compute_firewall" "allow_health_check" {
  name    = "${var.name_prefix}-allow-health-check"
  network = google_compute_network.main.name

  allow {
    protocol = "tcp"
    ports    = ["80", "443", "8080"]
  }

  # Google Cloud health check ranges
  source_ranges = ["130.211.0.0/22", "35.191.0.0/16"]
  target_tags   = ["http-server", "https-server"]
  priority      = 1000
}

# ============================================
# Cloud NAT for outbound connectivity
# ============================================

resource "google_compute_router" "main" {
  name    = "${var.name_prefix}-router"
  network = google_compute_network.main.name
  region  = var.region

  bgp {
    asn = 64514
  }
}

resource "google_compute_router_nat" "main" {
  name   = "${var.name_prefix}-nat"
  router = google_compute_router.main.name
  region = var.region

  nat_ip_allocate_option             = "AUTO_ONLY"
  source_subnetwork_ip_ranges_to_nat = "ALL_SUBNETWORKS_ALL_IP_RANGES"

  log_config {
    enable = var.enable_nat_logs
    filter = "ERRORS_ONLY"
  }
}
