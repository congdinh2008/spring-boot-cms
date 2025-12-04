#!/bin/bash
# ============================================
# Terraform Deployment Script
# ============================================
# Usage: ./deploy.sh <environment> [command]
# Example: ./deploy.sh dev plan
#          ./deploy.sh prod apply

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Parse arguments
ENVIRONMENT=${1:-""}
COMMAND=${2:-"plan"}

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(dev|staging|prod)$ ]]; then
    echo -e "${RED}Error: Invalid environment. Use: dev, staging, or prod${NC}"
    echo "Usage: $0 <environment> [command]"
    exit 1
fi

# Validate command
if [[ ! "$COMMAND" =~ ^(init|plan|apply|destroy|output|validate)$ ]]; then
    echo -e "${RED}Error: Invalid command. Use: init, plan, apply, destroy, output, validate${NC}"
    exit 1
fi

# Navigate to environment directory
ENV_DIR="$SCRIPT_DIR/environments/$ENVIRONMENT"
if [ ! -d "$ENV_DIR" ]; then
    echo -e "${RED}Error: Environment directory not found: $ENV_DIR${NC}"
    exit 1
fi

cd "$ENV_DIR"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Environment: ${YELLOW}$ENVIRONMENT${NC}"
echo -e "${GREEN}Command: ${YELLOW}$COMMAND${NC}"
echo -e "${GREEN}Directory: ${YELLOW}$ENV_DIR${NC}"
echo -e "${GREEN}========================================${NC}"

# Check for terraform.tfvars
if [ ! -f "terraform.tfvars" ] && [ "$COMMAND" != "init" ]; then
    echo -e "${YELLOW}Warning: terraform.tfvars not found.${NC}"
    echo -e "Copy terraform.tfvars.example to terraform.tfvars and configure it."
    
    if [ "$COMMAND" != "validate" ]; then
        exit 1
    fi
fi

# Production safety check
if [ "$ENVIRONMENT" == "prod" ] && [ "$COMMAND" == "destroy" ]; then
    echo -e "${RED}========================================${NC}"
    echo -e "${RED}WARNING: You are about to DESTROY production!${NC}"
    echo -e "${RED}========================================${NC}"
    read -p "Type 'destroy-production' to confirm: " CONFIRM
    if [ "$CONFIRM" != "destroy-production" ]; then
        echo "Aborted."
        exit 1
    fi
fi

# Execute Terraform command
case $COMMAND in
    init)
        echo -e "${GREEN}Initializing Terraform...${NC}"
        terraform init -upgrade
        ;;
    plan)
        echo -e "${GREEN}Planning Terraform changes...${NC}"
        terraform plan -out=tfplan
        ;;
    apply)
        if [ -f "tfplan" ]; then
            echo -e "${GREEN}Applying Terraform plan...${NC}"
            terraform apply tfplan
            rm -f tfplan
        else
            echo -e "${GREEN}Applying Terraform changes...${NC}"
            terraform apply
        fi
        ;;
    destroy)
        echo -e "${RED}Destroying Terraform resources...${NC}"
        terraform destroy
        ;;
    output)
        echo -e "${GREEN}Terraform outputs:${NC}"
        terraform output
        ;;
    validate)
        echo -e "${GREEN}Validating Terraform configuration...${NC}"
        terraform validate
        ;;
esac

echo -e "${GREEN}Done!${NC}"
