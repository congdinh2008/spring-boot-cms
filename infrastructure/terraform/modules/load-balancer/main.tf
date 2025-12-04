# ============================================
# Load Balancer Module
# ============================================
# Creates Global Load Balancer with Cloud CDN and SSL

# Reserve Global IP Address
resource "google_compute_global_address" "main" {
  name        = "${var.name_prefix}-lb-ip"
  description = "Global IP for ${var.name_prefix} load balancer"
}

# Managed SSL Certificate
resource "google_compute_managed_ssl_certificate" "main" {
  count = length(var.domains) > 0 ? 1 : 0
  name  = "${var.name_prefix}-ssl-cert"

  managed {
    domains = var.domains
  }
}

# Backend NEG for Backend Service
resource "google_compute_region_network_endpoint_group" "backend" {
  name                  = "${var.name_prefix}-backend-neg"
  network_endpoint_type = "SERVERLESS"
  region                = var.region

  cloud_run {
    service = var.backend_service_name
  }
}

# Backend NEG for Frontend Service
resource "google_compute_region_network_endpoint_group" "frontend" {
  name                  = "${var.name_prefix}-frontend-neg"
  network_endpoint_type = "SERVERLESS"
  region                = var.region

  cloud_run {
    service = var.frontend_service_name
  }
}

# Backend Service - Backend API
resource "google_compute_backend_service" "backend" {
  name                  = "${var.name_prefix}-backend-service"
  protocol              = "HTTPS"
  port_name             = "http"
  timeout_sec           = 30
  load_balancing_scheme = "EXTERNAL_MANAGED"
  security_policy       = var.backend_security_policy

  backend {
    group = google_compute_region_network_endpoint_group.backend.id
  }

  log_config {
    enable      = true
    sample_rate = var.log_sample_rate
  }

  # Cloud CDN disabled for API (dynamic content)
  enable_cdn = false
}

# Backend Service - Frontend with CDN
resource "google_compute_backend_service" "frontend" {
  name                  = "${var.name_prefix}-frontend-service"
  protocol              = "HTTPS"
  port_name             = "http"
  timeout_sec           = 30
  load_balancing_scheme = "EXTERNAL_MANAGED"
  security_policy       = var.frontend_security_policy

  backend {
    group = google_compute_region_network_endpoint_group.frontend.id
  }

  log_config {
    enable      = true
    sample_rate = var.log_sample_rate
  }

  # Cloud CDN enabled for static content
  enable_cdn = var.enable_cdn

  dynamic "cdn_policy" {
    for_each = var.enable_cdn ? [1] : []
    content {
      cache_mode                   = "CACHE_ALL_STATIC"
      default_ttl                  = var.cdn_default_ttl
      max_ttl                      = var.cdn_max_ttl
      client_ttl                   = var.cdn_client_ttl
      negative_caching             = true
      serve_while_stale            = 86400
      signed_url_cache_max_age_sec = 7200

      cache_key_policy {
        include_host         = true
        include_protocol     = true
        include_query_string = true
      }
    }
  }
}

# URL Map - Route requests to appropriate backends
resource "google_compute_url_map" "main" {
  name            = "${var.name_prefix}-url-map"
  default_service = google_compute_backend_service.frontend.id

  host_rule {
    hosts        = ["*"]
    path_matcher = "main"
  }

  path_matcher {
    name            = "main"
    default_service = google_compute_backend_service.frontend.id

    # API routes go to backend
    path_rule {
      paths   = ["/api/*", "/actuator/*"]
      service = google_compute_backend_service.backend.id
    }

    # Everything else goes to frontend
    path_rule {
      paths   = ["/*"]
      service = google_compute_backend_service.frontend.id
    }
  }
}

# HTTPS Proxy
resource "google_compute_target_https_proxy" "main" {
  count            = length(var.domains) > 0 ? 1 : 0
  name             = "${var.name_prefix}-https-proxy"
  url_map          = google_compute_url_map.main.id
  ssl_certificates = [google_compute_managed_ssl_certificate.main[0].id]
}

# HTTP Proxy (for redirect)
resource "google_compute_target_http_proxy" "redirect" {
  count   = var.enable_http_redirect ? 1 : 0
  name    = "${var.name_prefix}-http-redirect-proxy"
  url_map = google_compute_url_map.http_redirect[0].id
}

# HTTP to HTTPS Redirect URL Map
resource "google_compute_url_map" "http_redirect" {
  count = var.enable_http_redirect ? 1 : 0
  name  = "${var.name_prefix}-http-redirect"

  default_url_redirect {
    https_redirect         = true
    redirect_response_code = "MOVED_PERMANENTLY_DEFAULT"
    strip_query            = false
  }
}

# HTTPS Forwarding Rule
resource "google_compute_global_forwarding_rule" "https" {
  count                 = length(var.domains) > 0 ? 1 : 0
  name                  = "${var.name_prefix}-https-rule"
  ip_protocol           = "TCP"
  port_range            = "443"
  target                = google_compute_target_https_proxy.main[0].id
  ip_address            = google_compute_global_address.main.id
  load_balancing_scheme = "EXTERNAL_MANAGED"
}

# HTTP Forwarding Rule (for redirect)
resource "google_compute_global_forwarding_rule" "http" {
  count                 = var.enable_http_redirect ? 1 : 0
  name                  = "${var.name_prefix}-http-rule"
  ip_protocol           = "TCP"
  port_range            = "80"
  target                = google_compute_target_http_proxy.redirect[0].id
  ip_address            = google_compute_global_address.main.id
  load_balancing_scheme = "EXTERNAL_MANAGED"
}

# HTTP Target Proxy (without SSL - for direct IP access)
resource "google_compute_target_http_proxy" "main" {
  count   = length(var.domains) == 0 ? 1 : 0
  name    = "${var.name_prefix}-http-proxy"
  url_map = google_compute_url_map.main.id
}

# HTTP Forwarding Rule (without SSL)
resource "google_compute_global_forwarding_rule" "http_direct" {
  count                 = length(var.domains) == 0 ? 1 : 0
  name                  = "${var.name_prefix}-http-direct-rule"
  ip_protocol           = "TCP"
  port_range            = "80"
  target                = google_compute_target_http_proxy.main[0].id
  ip_address            = google_compute_global_address.main.id
  load_balancing_scheme = "EXTERNAL_MANAGED"
}
