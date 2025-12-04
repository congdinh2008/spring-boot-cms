# CMS - Content Management System

A full-stack Content Management System built with Spring Boot backend and React TypeScript frontend, deployed on Google Cloud Platform using Terraform.

## 🏗️ Project Structure

```
cms/
├── backend/                    # Spring Boot API
│   ├── src/
│   │   └── main/
│   │       ├── java/          # Java source code
│   │       │   └── com/congdinh/cms/
│   │       │       ├── config/        # Security, CORS configs
│   │       │       ├── controllers/   # REST controllers
│   │       │       ├── dtos/          # Data Transfer Objects
│   │       │       ├── entities/      # JPA entities
│   │       │       ├── repositories/  # Spring Data repos
│   │       │       ├── services/      # Business logic
│   │       │       └── exceptions/    # Exception handling
│   │       └── resources/     # Configuration files
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                   # React + TypeScript + Vite
│   ├── src/
│   │   ├── api/               # API client (Axios)
│   │   ├── components/        # React components
│   │   │   ├── ui/           # Reusable UI components
│   │   │   ├── layout/       # Header, Footer, Layout
│   │   │   └── auth/         # Auth components
│   │   ├── hooks/             # Custom React hooks
│   │   ├── pages/             # Page components
│   │   ├── stores/            # Zustand stores
│   │   └── types/             # TypeScript types
│   ├── package.json
│   ├── Dockerfile
│   ├── nginx.conf             # Local development nginx
│   ├── nginx.prod.conf        # Production nginx with API proxy
│   └── docker-entrypoint.sh   # Runtime config script
├── infrastructure/
│   └── terraform/
│       ├── modules/           # Reusable Terraform modules
│       │   ├── network/       # VPC, Subnets, NAT
│       │   ├── database/      # Cloud SQL PostgreSQL
│       │   ├── cloud-run/     # Cloud Run services
│       │   ├── secrets/       # Secret Manager
│       │   ├── iam/           # Service accounts & roles
│       │   ├── artifact-registry/  # Container registry
│       │   └── security/      # Cloud Armor (prod only)
│       └── environments/      # Environment configs
│           ├── dev/
│           ├── staging/
│           └── prod/
├── .github/
│   └── workflows/             # GitHub Actions CI/CD
├── docker-compose.yml         # Full-stack Docker setup
└── README.md
```

## 🚀 Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.0 (Java 21)
- **Security**: Spring Security 7.0 + JWT Authentication
- **Database**: PostgreSQL 16/17
- **ORM**: Spring Data JPA / Hibernate
- **API Documentation**: OpenAPI 3.0 (SpringDoc)
- **Build**: Maven
- **Container**: Docker with multi-stage build

### Frontend
- **Framework**: React 19.2 + TypeScript 5.9
- **Build Tool**: Vite 7
- **State Management**: 
  - TanStack Query v5 (Server State)
  - Zustand (Client State)
- **Styling**: Tailwind CSS 4.0
- **Routing**: React Router v7
- **HTTP Client**: Axios
- **UI Components**: Custom components (Button, Input, Modal, Table, etc.)

### Infrastructure (GCP)
- **Compute**: Cloud Run (Serverless, Gen2)
- **Database**: Cloud SQL PostgreSQL 16 (Private IP)
- **Networking**: VPC, VPC Access Connector, Cloud NAT
- **Container Registry**: Artifact Registry
- **Secrets**: Secret Manager
- **Security**: Cloud Armor (Production)
- **IaC**: Terraform (Modular architecture)

## 🛠️ Development Setup

### Prerequisites
- Java 21+
- Node.js 20+
- Docker & Docker Compose
- PostgreSQL 16+ (or use Docker)

### Backend Development

```bash
cd backend

# Option 1: Start PostgreSQL with Docker
docker run -d \
  --name cms-postgres \
  -e POSTGRES_DB=cms_dev_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine

# Run application
./mvnw spring-boot:run

# API available at http://localhost:8080
# Swagger UI at http://localhost:8080/swagger-ui.html
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Create .env from example
cp .env.example .env

# Start dev server (proxies /api to backend)
npm run dev

# App available at http://localhost:5173
```

### Full Stack with Docker Compose

```bash
# From root directory
docker-compose up -d

# Services:
# - Frontend: http://localhost (port 80)
# - Backend API: http://localhost:8080
# - PostgreSQL: localhost:5432
```

## 📦 Production Deployment (GCP)

### Prerequisites
- GCP Project with billing enabled
- `gcloud` CLI authenticated
- Terraform >= 1.5
- Docker

### Deploy Infrastructure

```bash
cd infrastructure/terraform/environments/dev

# Initialize Terraform
terraform init

# Create terraform.tfvars
cp terraform.tfvars.example terraform.tfvars
# Edit with your GCP project details

# Preview changes
terraform plan

# Deploy
terraform apply
```

### Build and Push Docker Images

**Important**: For Mac M1/M2, must build with `--platform linux/amd64` for Cloud Run.

```bash
# Authenticate Docker with GCP
gcloud auth configure-docker asia-southeast1-docker.pkg.dev

# Build and push backend
cd backend
docker build --platform linux/amd64 \
  -t asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-dev-images/backend:latest .
docker push asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-dev-images/backend:latest

# Build and push frontend
cd ../frontend
docker build --platform linux/amd64 \
  -t asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-dev-images/frontend:latest .
docker push asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-dev-images/frontend:latest

# Redeploy Cloud Run services
cd ../infrastructure/terraform/environments/dev
terraform apply -auto-approve
```

### Destroy Resources

```bash
cd infrastructure/terraform/environments/dev

# Recommended: Destroy in order to avoid dependency issues
terraform destroy -target=module.cloud_run -auto-approve
terraform destroy -target=module.database -auto-approve
terraform destroy -auto-approve
```

## 🔐 API Endpoints

### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login, get JWT token | ❌ |
| GET | `/api/auth/me` | Get current user profile | ✅ |

### Categories
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/categories` | List categories (paginated) | ❌ |
| GET | `/api/v1/categories/:id` | Get category by ID | ❌ |
| POST | `/api/v1/categories` | Create category | ✅ Admin |
| PUT | `/api/v1/categories/:id` | Update category | ✅ Admin |
| DELETE | `/api/v1/categories/:id` | Delete category | ✅ Admin |

### News
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/news` | List published news | ❌ |
| GET | `/api/v1/news/:id` | Get news detail | ❌ |
| GET | `/api/v1/news/my-news` | List user's news | ✅ |
| POST | `/api/v1/news` | Create news | ✅ |
| PUT | `/api/v1/news/my-news/:id` | Update own news | ✅ |
| DELETE | `/api/v1/news/my-news/:id` | Delete own news | ✅ |
| PATCH | `/api/v1/news/my-news/:id/publish` | Publish news | ✅ |
| PATCH | `/api/v1/news/my-news/:id/archive` | Archive news | ✅ |

### Admin (Admin role required)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/admin/news` | List all news | ✅ Admin |
| PUT | `/api/v1/admin/news/:id` | Update any news | ✅ Admin |
| DELETE | `/api/v1/admin/news/:id` | Delete any news | ✅ Admin |

## 🔧 Environment Variables

### Backend
| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile | `dev` |
| `SPRING_DATASOURCE_URL` | JDBC connection URL | `jdbc:postgresql://localhost:5432/cms_dev_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | JWT signing key (base64) | - |
| `JWT_EXPIRATION` | Token expiration (ms) | `3600000` |
| `CORS_ORIGINS` | Allowed CORS origins | `http://localhost:5173` |

### Frontend
| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | API base URL | `/api` |
| `BACKEND_URL` | Backend URL (prod nginx) | Set by Terraform |

## 🏛️ Architecture

### Production Architecture (GCP)

```
                    ┌─────────────────────────────────────────────────────────┐
                    │                     Google Cloud Platform               │
                    │                                                         │
   Users ──────────►│  ┌─────────────────┐      ┌─────────────────┐         │
                    │  │   Cloud Run     │      │   Cloud Run     │         │
                    │  │   (Frontend)    │─────►│   (Backend)     │         │
                    │  │   nginx + React │ /api │   Spring Boot   │         │
                    │  └─────────────────┘      └────────┬────────┘         │
                    │                                    │                   │
                    │                           ┌────────▼────────┐         │
                    │                           │  VPC Connector  │         │
                    │                           └────────┬────────┘         │
                    │                                    │ Private IP       │
                    │                           ┌────────▼────────┐         │
                    │                           │    Cloud SQL    │         │
                    │                           │   PostgreSQL    │         │
                    │                           └─────────────────┘         │
                    │                                                         │
                    │  ┌─────────────────┐      ┌─────────────────┐         │
                    │  │ Secret Manager  │      │Artifact Registry│         │
                    │  │ (DB Pass, JWT)  │      │ (Docker Images) │         │
                    │  └─────────────────┘      └─────────────────┘         │
                    └─────────────────────────────────────────────────────────┘
```

### CORS Strategy

- **Frontend nginx** handles CORS for `/api/*` requests
- **Backend CORS** is for direct access (Swagger UI, local dev)
- **Nginx removes Origin header** when proxying to backend to avoid double CORS handling

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Type Check
```bash
cd frontend
npm run type-check
npm run lint
```

## 📚 Documentation

- **API Documentation**: `/swagger-ui.html` (when backend is running)
- **Terraform Documentation**: [infrastructure/terraform/README.md](infrastructure/terraform/README.md)

## 🐛 Troubleshooting

### Common Issues

1. **Docker build fails on Mac M1/M2**
   - Use `--platform linux/amd64` flag

2. **Cloud Run can't connect to Cloud SQL**
   - Ensure VPC Connector is configured
   - Check `SPRING_DATASOURCE_URL` uses private IP

3. **CORS errors in browser**
   - Frontend nginx handles CORS for `/api/*`
   - Check `BACKEND_URL` environment variable

4. **Terraform destroy hangs**
   - Destroy Cloud Run first: `terraform destroy -target=module.cloud_run`
   - Then destroy database and remaining resources

## 📄 License

This project is licensed under the MIT License.
