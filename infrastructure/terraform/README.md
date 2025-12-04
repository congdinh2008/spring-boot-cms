# CMS Infrastructure - Terraform Modules

Terraform modular infrastructure để triển khai hệ thống CMS lên Google Cloud Platform với hỗ trợ multi-environment (dev, staging, prod).

## 📋 Kiến trúc hệ thống

```
                                    ┌─────────────────────────────────────────────────────────────┐
                                    │                         Internet                            │
                                    └─────────────────────────────────────────────────────────────┘
                                                              │
                                                              ▼
                                    ┌─────────────────────────────────────────────────────────────┐
                                    │              Global HTTP(S) Load Balancer                   │
                                    │            + Cloud Armor (WAF) + Cloud CDN                  │
                                    │                    (Production only)                        │
                                    └─────────────────────────────────────────────────────────────┘
                                                              │
                                           ┌──────────────────┴──────────────────┐
                                           │                                      │
                                           ▼                                      ▼
                              ┌─────────────────────────┐          ┌─────────────────────────┐
                              │   Cloud Run (Frontend)  │          │   Cloud Run (Backend)   │
                              │   - React + TypeScript  │          │   - Spring Boot 4.0     │
                              │   - Nginx               │          │   - REST API            │
                              │   - Auto-scaling        │          │   - JWT Auth            │
                              └─────────────────────────┘          └─────────────────────────┘
                                                                               │
                                                                               │ VPC Connector
                                                                               ▼
                              ┌─────────────────────────────────────────────────────────────────┐
                              │                          VPC Network                             │
                              │  ┌─────────────────────────────────────────────────────────────┐ │
                              │  │                      Private Subnet                          │ │
                              │  │   ┌──────────────────────────────────────────────────────┐  │ │
                              │  │   │              Cloud SQL (PostgreSQL 16)               │  │ │
                              │  │   │  - Regional HA (Production)                          │  │ │
                              │  │   │  - Private IP only                                   │  │ │
                              │  │   │  - Automated backups + PITR                          │  │ │
                              │  │   └──────────────────────────────────────────────────────┘  │ │
                              │  └─────────────────────────────────────────────────────────────┘ │
                              └─────────────────────────────────────────────────────────────────┘
```

## 📁 Cấu trúc thư mục

```
infrastructure/terraform/
├── modules/                          # Reusable Terraform Modules
│   ├── network/                      # VPC, Subnet, NAT, VPC Connector
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── database/                     # Cloud SQL PostgreSQL
│   ├── secrets/                      # Secret Manager
│   ├── iam/                          # Service Accounts & IAM
│   ├── artifact-registry/            # Container Registry
│   ├── cloud-run/                    # Cloud Run Services
│   ├── security/                     # Cloud Armor WAF
│   ├── monitoring/                   # Uptime Checks, Alerts, Dashboard
│   └── load-balancer/                # Global LB, CDN, SSL
├── environments/                     # Environment Configurations
│   ├── dev/                          # Development
│   │   ├── main.tf                   # Module composition
│   │   ├── variables.tf              # Environment variables
│   │   ├── outputs.tf                # Environment outputs
│   │   └── terraform.tfvars.example  # Example config
│   ├── staging/                      # Staging/UAT
│   └── prod/                         # Production
├── deploy.sh                         # Deployment helper script
├── Makefile                          # Make commands
└── README.md                         # This file
```

## 🚀 Quick Start

### Prerequisites

- **GCP Project** với billing enabled
- **gcloud CLI** đã cài đặt và authenticate
- **Terraform** >= 1.5.0
- **Docker** để build images

### 1. Authenticate với GCP

```bash
# Login to GCP
gcloud auth login
gcloud auth application-default login

# Set project
gcloud config set project YOUR_PROJECT_ID
```

### 2. Configure Environment

```bash
# Navigate to desired environment
cd environments/dev  # or staging, prod

# Copy và edit configuration
cp terraform.tfvars.example terraform.tfvars
vim terraform.tfvars
```

**Minimum configuration** (`terraform.tfvars`):

```hcl
project_id = "your-gcp-project-id"
region     = "asia-southeast1"
jwt_secret = "your-jwt-secret-at-least-32-characters-long"
```

### 3. Deploy Infrastructure

**Option 1: Using Makefile (Recommended)**

```bash
# From terraform root directory
make init ENV=dev
make plan ENV=dev
make apply ENV=dev
```

**Option 2: Using deploy.sh**

```bash
./deploy.sh dev init
./deploy.sh dev plan
./deploy.sh dev apply
```

**Option 3: Direct Terraform**

```bash
cd environments/dev
terraform init
terraform plan
terraform apply
```

### 4. Build & Push Docker Images

```bash
# Get Artifact Registry URL
terraform output artifact_registry_url

# Configure Docker
gcloud auth configure-docker asia-southeast1-docker.pkg.dev

# Build and push backend
cd backend
docker build -t asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-dev-images/backend:v1.0.0 .
docker push asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-dev-images/backend:v1.0.0

# Build and push frontend
cd ../frontend
docker build -t asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-dev-images/frontend:v1.0.0 .
docker push asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-dev-images/frontend:v1.0.0
```

## 📊 Environment Comparison

| Feature | Dev | Staging | Prod |
|---------|-----|---------|------|
| **Database Tier** | db-f1-micro | db-custom-2-4096 | db-custom-4-8192 |
| **Database HA** | ZONAL | ZONAL | REGIONAL |
| **Backups** | Disabled | 7 days | 30 days |
| **Read Replica** | No | No | Optional |
| **Min Instances** | 0 | 1 | 2 |
| **Max Instances** | 2 | 5 | 20 |
| **CPU Idle** | Yes | Yes | No |
| **Cloud Armor** | No | Yes | Yes |
| **Load Balancer** | No | No | Yes |
| **CDN** | No | No | Yes |
| **Monitoring** | Basic | Yes | Full |
| **Deletion Protection** | No | No | Yes |
| **Est. Cost/Month** | ~$15 | ~$75 | ~$330+ |

## 🧩 Modules

### Network Module
Tạo VPC network, subnet, VPC connector cho Cloud Run, Cloud NAT, và private service connection cho Cloud SQL.

**Key Resources:**
- `google_compute_network` - VPC Network
- `google_compute_subnetwork` - Private Subnet
- `google_vpc_access_connector` - Cloud Run VPC Connector
- `google_compute_router_nat` - Cloud NAT
- `google_service_networking_connection` - Cloud SQL Private Connection

### Database Module
Tạo Cloud SQL PostgreSQL instance với configurable HA, backups, và read replicas.

**Features:**
- PostgreSQL 16
- Configurable instance tier
- Private IP only (no public access)
- Automated backups với PITR
- Query Insights
- Optional read replica

### Secrets Module
Quản lý secrets trong Secret Manager.

**Secrets:**
- Database password
- JWT signing secret

### IAM Module
Tạo service accounts với least-privilege permissions.

**Service Accounts:**
- Cloud Run service account
- CI/CD service account (optional)

### Artifact Registry Module
Tạo Docker repository với cleanup policies.

**Features:**
- Docker container registry
- Automatic cleanup of old images
- IAM bindings cho readers/writers

### Cloud Run Module
Tạo Cloud Run services cho backend và frontend.

**Features:**
- Gen2 execution environment
- VPC connector integration
- Secret environment variables
- Health checks
- Auto-scaling

### Security Module
Tạo Cloud Armor security policies.

**Protection:**
- OWASP Top 10 rules (SQLi, XSS, RCE, LFI, RFI)
- Rate limiting
- DDoS protection
- Country blocking
- IP whitelist/blacklist

### Monitoring Module
Tạo monitoring, alerting, và dashboards.

**Features:**
- Uptime checks
- Alert policies (downtime, errors, latency)
- Email & Slack notifications
- Custom dashboard

### Load Balancer Module
Tạo Global HTTPS Load Balancer với CDN.

**Features:**
- Global Load Balancer
- Managed SSL certificates
- Cloud CDN cho static content
- HTTP to HTTPS redirect

## 🔧 Makefile Commands

```bash
make help                    # Show all commands

# Environment-specific
make init ENV=dev            # Initialize Terraform
make plan ENV=dev            # Plan changes
make apply ENV=dev           # Apply changes
make destroy ENV=dev         # Destroy resources
make output ENV=dev          # Show outputs

# Shortcuts
make dev-plan                # Plan dev environment
make dev-apply               # Apply dev environment
make staging-plan            # Plan staging
make prod-plan               # Plan production

# Utilities
make fmt                     # Format all Terraform files
make validate ENV=dev        # Validate configuration
make clean                   # Clean Terraform cache
make setup-state             # Create GCS bucket for remote state
```

## 🔐 Remote State (Recommended)

### 1. Create GCS Bucket

```bash
PROJECT_ID="your-project-id"
gsutil mb -l asia-southeast1 gs://${PROJECT_ID}-terraform-state
gsutil versioning set on gs://${PROJECT_ID}-terraform-state
```

### 2. Enable Remote State

Uncomment backend block trong `environments/<env>/main.tf`:

```hcl
terraform {
  backend "gcs" {
    bucket = "your-project-id-terraform-state"
    prefix = "cms/dev"  # or staging, prod
  }
}
```

### 3. Migrate State

```bash
terraform init -migrate-state
```

## 🔄 CI/CD Integration

### GitHub Actions

File `.github/workflows/deploy.yml` đã được tạo sẵn với:

1. **Test stage**: Chạy unit tests
2. **Build stage**: Build Docker images
3. **Push stage**: Push to Artifact Registry
4. **Deploy stage**: Deploy to Cloud Run
5. **Health check stage**: Verify deployment

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `GCP_PROJECT_ID` | GCP Project ID |
| `GCP_SA_KEY` | Service Account JSON key |
| `JWT_SECRET` | JWT signing secret |
| `SLACK_WEBHOOK_URL` | (Optional) Slack notifications |

## 🌐 Custom Domain Setup (Production)

1. **Configure domains** trong `terraform.tfvars`:

```hcl
domains = ["cms.yourdomain.com", "www.cms.yourdomain.com"]
```

2. **Apply Terraform**:

```bash
make apply ENV=prod
```

3. **Get Load Balancer IP**:

```bash
terraform output load_balancer_ip
```

4. **Configure DNS**:

```
cms.yourdomain.com     A     <load_balancer_ip>
www.cms.yourdomain.com A     <load_balancer_ip>
```

5. SSL certificate sẽ tự động được provisioned (15-30 phút)

## 💰 Cost Estimation

| Resource | Dev | Staging | Prod |
|----------|-----|---------|------|
| Cloud SQL | ~$10 | ~$50 | ~$150+ |
| Cloud Run | ~$0-5 | ~$15-30 | ~$50-100+ |
| VPC Connector | ~$7 | ~$7 | ~$14 |
| Cloud NAT | ~$3 | ~$3 | ~$3 |
| Load Balancer | - | - | ~$20 |
| Cloud Armor | - | ~$5 | ~$10+ |
| Secrets | <$1 | <$1 | <$1 |
| Monitoring | <$1 | ~$5 | ~$10+ |
| **Total** | **~$20** | **~$85** | **~$350+** |

*Costs dựa trên asia-southeast1 region. Actual costs phụ thuộc vào traffic và usage.*

## 🔐 Security Best Practices

1. **Never commit `terraform.tfvars`** - Sử dụng `.gitignore`
2. **Use remote state** với encryption
3. **Enable deletion protection** cho production
4. **Rotate secrets** định kỳ
5. **Review IAM permissions** thường xuyên
6. **Enable Cloud Armor** cho public endpoints
7. **Use VPC connector** để access database qua private IP

## 🧹 Cleanup

### Destroy Order (Recommended)

Để tránh lỗi dependency khi destroy, hãy xóa theo thứ tự:

```bash
cd environments/dev

# Step 1: Destroy Cloud Run services (terminate DB connections)
terraform destroy -target=module.cloud_run -auto-approve

# Step 2: Destroy database
terraform destroy -target=module.database -auto-approve

# Step 3: Destroy remaining resources
terraform destroy -auto-approve
```

### Common Destroy Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| Database in use | Cloud Run still has connections | Destroy Cloud Run first |
| VPC Connector hangs | Cloud Run not fully terminated | Wait 2-3 mins, or use gcloud to delete |
| Service Networking stuck | Cloud SQL just deleted | Remove from state: `terraform state rm module.network.google_service_networking_connection.private_vpc_connection` |
| State lock error | Previous terraform crashed | `terraform force-unlock LOCK_ID` |

### Force Cleanup (Emergency)

Nếu terraform destroy bị treo:

```bash
# Kill terraform process
# Ctrl+C or kill the process

# Remove lock file if needed
rm -f .terraform.tfstate.lock.info

# Remove stuck resource from state
terraform state rm <resource_address>

# Delete resource manually via gcloud
gcloud sql instances delete INSTANCE_NAME --quiet
gcloud compute networks vpc-access connectors delete CONNECTOR_NAME --region=REGION --quiet

# Continue destroy
terraform destroy -auto-approve
```

### Production Destroy

```bash
# Production (có confirmation)
make destroy ENV=prod
# Type 'destroy-production' to confirm
```

> **Note:** Nếu `deletion_protection = true`, cần disable trước khi destroy.

## 🐛 Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| API not enabled | Chạy `terraform apply` lại hoặc enable APIs thủ công |
| Quota exceeded | Request quota increase trong GCP Console |
| Permission denied | Check IAM roles cho account của bạn |
| Network timeout | Kiểm tra VPC connector configuration |
| Database connection | Verify private IP và firewall rules |

### Useful Commands

```bash
# View state
terraform state list

# Import existing resource
terraform import module.database.google_sql_database_instance.main \
  projects/PROJECT/instances/INSTANCE

# Force unlock state
terraform force-unlock LOCK_ID

# Refresh state
terraform refresh
```

## 📚 References

- [Cloud Run Documentation](https://cloud.google.com/run/docs)
- [Cloud SQL Documentation](https://cloud.google.com/sql/docs)
- [Cloud Armor Documentation](https://cloud.google.com/armor/docs)
- [Terraform GCP Provider](https://registry.terraform.io/providers/hashicorp/google/latest/docs)
- [GCP Best Practices](https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations)
