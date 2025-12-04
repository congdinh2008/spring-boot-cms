# ============================================
# Security Module (Cloud Armor)
# ============================================
# Creates Cloud Armor security policies

# Cloud Armor Security Policy for Backend
resource "google_compute_security_policy" "backend" {
  name        = "${var.name_prefix}-backend-policy"
  description = "Cloud Armor security policy for backend"

  # Default rule - allow all
  rule {
    action   = "allow"
    priority = 2147483647
    match {
      versioned_expr = "SRC_IPS_V1"
      config {
        src_ip_ranges = ["*"]
      }
    }
    description = "Default rule - allow all traffic"
  }

  # Rate limiting rule
  dynamic "rule" {
    for_each = var.enable_rate_limiting ? [1] : []
    content {
      action   = "rate_based_ban"
      priority = 1000

      match {
        versioned_expr = "SRC_IPS_V1"
        config {
          src_ip_ranges = ["*"]
        }
      }

      rate_limit_options {
        conform_action = "allow"
        exceed_action  = "deny(429)"

        rate_limit_threshold {
          count        = var.rate_limit_requests
          interval_sec = var.rate_limit_interval
        }

        ban_duration_sec = var.ban_duration
      }

      description = "Rate limiting - ${var.rate_limit_requests} requests per ${var.rate_limit_interval}s"
    }
  }

  # Block common attack patterns (OWASP rules)
  dynamic "rule" {
    for_each = var.enable_owasp_rules ? [1] : []
    content {
      action   = "deny(403)"
      priority = 100

      match {
        expr {
          expression = "evaluatePreconfiguredExpr('sqli-stable')"
        }
      }

      description = "Block SQL injection attacks"
    }
  }

  dynamic "rule" {
    for_each = var.enable_owasp_rules ? [1] : []
    content {
      action   = "deny(403)"
      priority = 101

      match {
        expr {
          expression = "evaluatePreconfiguredExpr('xss-stable')"
        }
      }

      description = "Block XSS attacks"
    }
  }

  dynamic "rule" {
    for_each = var.enable_owasp_rules ? [1] : []
    content {
      action   = "deny(403)"
      priority = 102

      match {
        expr {
          expression = "evaluatePreconfiguredExpr('rce-stable')"
        }
      }

      description = "Block remote code execution attacks"
    }
  }

  dynamic "rule" {
    for_each = var.enable_owasp_rules ? [1] : []
    content {
      action   = "deny(403)"
      priority = 103

      match {
        expr {
          expression = "evaluatePreconfiguredExpr('lfi-stable')"
        }
      }

      description = "Block local file inclusion attacks"
    }
  }

  dynamic "rule" {
    for_each = var.enable_owasp_rules ? [1] : []
    content {
      action   = "deny(403)"
      priority = 104

      match {
        expr {
          expression = "evaluatePreconfiguredExpr('rfi-stable')"
        }
      }

      description = "Block remote file inclusion attacks"
    }
  }

  dynamic "rule" {
    for_each = var.enable_owasp_rules ? [1] : []
    content {
      action   = "deny(403)"
      priority = 105

      match {
        expr {
          expression = "evaluatePreconfiguredExpr('methodenforcement-stable')"
        }
      }

      description = "Block protocol attacks"
    }
  }

  dynamic "rule" {
    for_each = var.enable_owasp_rules ? [1] : []
    content {
      action   = "deny(403)"
      priority = 106

      match {
        expr {
          expression = "evaluatePreconfiguredExpr('scannerdetection-stable')"
        }
      }

      description = "Block scanner detection"
    }
  }

  # Block specific countries (optional)
  dynamic "rule" {
    for_each = length(var.blocked_countries) > 0 ? [1] : []
    content {
      action   = "deny(403)"
      priority = 200

      match {
        expr {
          expression = "origin.region_code in [${join(",", [for c in var.blocked_countries : format("'%s'", c)])}]"
        }
      }

      description = "Block traffic from specific countries"
    }
  }

  # Allow specific IPs (whitelist)
  dynamic "rule" {
    for_each = length(var.allowed_ips) > 0 ? [1] : []
    content {
      action   = "allow"
      priority = 50

      match {
        versioned_expr = "SRC_IPS_V1"
        config {
          src_ip_ranges = var.allowed_ips
        }
      }

      description = "Allow whitelisted IPs"
    }
  }

  # Block specific IPs (blacklist)
  dynamic "rule" {
    for_each = length(var.blocked_ips) > 0 ? [1] : []
    content {
      action   = "deny(403)"
      priority = 300

      match {
        versioned_expr = "SRC_IPS_V1"
        config {
          src_ip_ranges = var.blocked_ips
        }
      }

      description = "Block blacklisted IPs"
    }
  }

  adaptive_protection_config {
    layer_7_ddos_defense_config {
      enable = var.enable_ddos_protection
    }
  }
}

# Cloud Armor Security Policy for Frontend
resource "google_compute_security_policy" "frontend" {
  name        = "${var.name_prefix}-frontend-policy"
  description = "Cloud Armor security policy for frontend"

  # Default rule - allow all
  rule {
    action   = "allow"
    priority = 2147483647
    match {
      versioned_expr = "SRC_IPS_V1"
      config {
        src_ip_ranges = ["*"]
      }
    }
    description = "Default rule - allow all traffic"
  }

  # Rate limiting
  dynamic "rule" {
    for_each = var.enable_rate_limiting ? [1] : []
    content {
      action   = "rate_based_ban"
      priority = 1000

      match {
        versioned_expr = "SRC_IPS_V1"
        config {
          src_ip_ranges = ["*"]
        }
      }

      rate_limit_options {
        conform_action = "allow"
        exceed_action  = "deny(429)"

        rate_limit_threshold {
          count        = var.frontend_rate_limit_requests
          interval_sec = var.rate_limit_interval
        }

        ban_duration_sec = var.ban_duration
      }

      description = "Rate limiting for frontend"
    }
  }

  # Block scanners on frontend too
  dynamic "rule" {
    for_each = var.enable_owasp_rules ? [1] : []
    content {
      action   = "deny(403)"
      priority = 100

      match {
        expr {
          expression = "evaluatePreconfiguredExpr('scannerdetection-stable')"
        }
      }

      description = "Block scanner detection"
    }
  }

  adaptive_protection_config {
    layer_7_ddos_defense_config {
      enable = var.enable_ddos_protection
    }
  }
}
