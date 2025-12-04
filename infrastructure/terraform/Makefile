# ============================================
# Terraform Makefile
# ============================================
# Usage: make <target> ENV=<environment>
# Example: make plan ENV=dev
#          make apply ENV=prod

.PHONY: help init plan apply destroy output validate fmt lint clean

# Default environment
ENV ?= dev

# Environment directory
ENV_DIR := environments/$(ENV)

# Colors
GREEN := \033[0;32m
YELLOW := \033[1;33m
RED := \033[0;31m
NC := \033[0m

help: ## Show this help
	@echo "Terraform Infrastructure Management"
	@echo ""
	@echo "Usage: make <target> ENV=<environment>"
	@echo ""
	@echo "Environments: dev, staging, prod"
	@echo ""
	@echo "Targets:"
	@awk 'BEGIN {FS = ":.*##"; printf ""} /^[a-zA-Z_-]+:.*?##/ { printf "  $(GREEN)%-15s$(NC) %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

init: ## Initialize Terraform
	@echo "$(GREEN)Initializing $(ENV)...$(NC)"
	cd $(ENV_DIR) && terraform init -upgrade

plan: ## Plan infrastructure changes
	@echo "$(GREEN)Planning $(ENV)...$(NC)"
	cd $(ENV_DIR) && terraform plan -out=tfplan

apply: ## Apply infrastructure changes
	@echo "$(GREEN)Applying $(ENV)...$(NC)"
	@if [ -f "$(ENV_DIR)/tfplan" ]; then \
		cd $(ENV_DIR) && terraform apply tfplan && rm -f tfplan; \
	else \
		cd $(ENV_DIR) && terraform apply; \
	fi

destroy: ## Destroy infrastructure (with confirmation for prod)
	@if [ "$(ENV)" = "prod" ]; then \
		echo "$(RED)WARNING: Destroying production!$(NC)"; \
		read -p "Type 'destroy-production' to confirm: " confirm; \
		if [ "$$confirm" = "destroy-production" ]; then \
			cd $(ENV_DIR) && terraform destroy; \
		else \
			echo "Aborted."; \
		fi \
	else \
		echo "$(YELLOW)Destroying $(ENV)...$(NC)"; \
		cd $(ENV_DIR) && terraform destroy; \
	fi

output: ## Show Terraform outputs
	@echo "$(GREEN)Outputs for $(ENV):$(NC)"
	cd $(ENV_DIR) && terraform output

validate: ## Validate Terraform configuration
	@echo "$(GREEN)Validating $(ENV)...$(NC)"
	cd $(ENV_DIR) && terraform validate

fmt: ## Format Terraform files
	@echo "$(GREEN)Formatting all Terraform files...$(NC)"
	terraform fmt -recursive

lint: ## Run TFLint
	@echo "$(GREEN)Linting all Terraform files...$(NC)"
	tflint --recursive

clean: ## Clean Terraform cache and plan files
	@echo "$(YELLOW)Cleaning Terraform cache...$(NC)"
	find . -type d -name ".terraform" -exec rm -rf {} + 2>/dev/null || true
	find . -type f -name "tfplan" -delete
	find . -type f -name ".terraform.lock.hcl" -delete

# Environment-specific shortcuts
dev-plan: ## Plan dev environment
	@$(MAKE) plan ENV=dev

dev-apply: ## Apply dev environment
	@$(MAKE) apply ENV=dev

staging-plan: ## Plan staging environment
	@$(MAKE) plan ENV=staging

staging-apply: ## Apply staging environment
	@$(MAKE) apply ENV=staging

prod-plan: ## Plan production environment
	@$(MAKE) plan ENV=prod

prod-apply: ## Apply production environment
	@$(MAKE) apply ENV=prod

# Utility targets
setup-state: ## Create GCS bucket for remote state
	@read -p "Enter project ID: " project; \
	gsutil mb -l asia-southeast1 gs://$$project-terraform-state; \
	gsutil versioning set on gs://$$project-terraform-state; \
	echo "$(GREEN)State bucket created: gs://$$project-terraform-state$(NC)"

graph: ## Generate dependency graph
	@echo "$(GREEN)Generating dependency graph for $(ENV)...$(NC)"
	cd $(ENV_DIR) && terraform graph | dot -Tpng > ../../graph-$(ENV).png
	@echo "Graph saved to graph-$(ENV).png"

cost: ## Estimate infrastructure cost (requires infracost)
	@echo "$(GREEN)Estimating cost for $(ENV)...$(NC)"
	cd $(ENV_DIR) && infracost breakdown --path .
