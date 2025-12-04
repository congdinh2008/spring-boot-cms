# CMS - Content Management System

A full-stack Content Management System built with Spring Boot backend and React TypeScript frontend, deployed on Google Cloud Platform.

## 🏗️ Project Structure

```
cms/
├── backend/                 # Spring Boot API
│   ├── src/
│   │   └── main/
│   │       ├── java/       # Java source code
│   │       └── resources/  # Configuration files
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                # React + TypeScript + Vite
│   ├── src/
│   │   ├── api/           # API client
│   │   ├── components/    # React components
│   │   ├── hooks/         # Custom React hooks
│   │   ├── pages/         # Page components
│   │   ├── stores/        # Zustand stores
│   │   └── types/         # TypeScript types
│   ├── package.json
│   ├── Dockerfile
│   └── nginx.conf
├── infrastructure/
│   └── terraform/          # GCP Terraform configs
├── docker-compose.yml       # Full-stack Docker setup
└── README.md
```

## 🚀 Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.0
- **Security**: Spring Security 7.0 + JWT Authentication
- **Database**: PostgreSQL 17
- **API Documentation**: OpenAPI 3.0 (Swagger)
- **Build**: Maven

### Frontend
- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite 7
- **State Management**: 
  - TanStack Query (Server State)
  - Zustand (Client State)
- **Styling**: Tailwind CSS 4.0
- **Routing**: React Router v6
- **HTTP Client**: Axios

### Infrastructure
- **Cloud**: Google Cloud Platform (GCP)
- **Compute**: Cloud Run (Serverless)
- **Database**: Cloud SQL (PostgreSQL 17)
- **Container Registry**: Artifact Registry
- **Secrets**: Secret Manager
- **IaC**: Terraform

## 🛠️ Development Setup

### Prerequisites
- Java 21+
- Node.js 20+
- Docker & Docker Compose
- PostgreSQL 17 (or use Docker)

### Backend Development

```bash
cd backend

# Start database with Docker
docker run -d \
  --name cms-postgres \
  -e POSTGRES_DB=cms \
  -e POSTGRES_USER=cms \
  -e POSTGRES_PASSWORD=cms123 \
  -p 5432:5432 \
  postgres:17-alpine

# Run application
./mvnw spring-boot:run

# API available at http://localhost:8080
# Swagger UI at http://localhost:8080/swagger-ui/index.html
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Start dev server (proxies /api to backend)
npm run dev

# App available at http://localhost:5173
```

### Full Stack with Docker Compose

```bash
# From root directory
cp .env.example .env  # Configure environment variables
docker-compose up -d

# Frontend: http://localhost (port 80)
# Backend API: http://localhost:8080
# Database: localhost:5432
```

## 📦 Production Deployment

### Deploy to GCP using Terraform

See [infrastructure/terraform/README.md](infrastructure/terraform/README.md) for detailed instructions.

Quick start:

```bash
cd infrastructure/terraform

# Initialize Terraform
terraform init

# Create terraform.tfvars from example
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars with your GCP project details

# Build and push Docker images
gcloud auth configure-docker asia-southeast1-docker.pkg.dev

# Backend
docker build -t asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-prod/backend:latest ../backend
docker push asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-prod/backend:latest

# Frontend
docker build -t asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-prod/frontend:latest ../frontend
docker push asia-southeast1-docker.pkg.dev/PROJECT_ID/cms-prod/frontend:latest

# Deploy infrastructure
terraform apply
```

## 🔐 API Features

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/me` - Get current user profile
- `POST /api/auth/refresh` - Refresh access token

### Categories (Protected)
- `GET /api/categories` - List categories (with pagination, search)
- `POST /api/categories` - Create category
- `PUT /api/categories/:id` - Update category
- `DELETE /api/categories/:id` - Delete category

### News
- `GET /api/news` - List published news (public)
- `GET /api/news/:id` - Get news detail (public)
- `GET /api/news/my-news` - List user's news (protected)
- `POST /api/news` - Create news (protected)
- `PUT /api/news/my-news/:id` - Update own news
- `DELETE /api/news/my-news/:id` - Delete own news
- `PATCH /api/news/my-news/:id/publish` - Publish news
- `PATCH /api/news/my-news/:id/archive` - Archive news

### Admin (Admin only)
- `GET /api/admin/news` - List all news
- `PUT /api/admin/news/:id` - Update any news
- `DELETE /api/admin/news/:id` - Delete any news

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
```

## 📝 License

MIT License
terraform init

# Plan deployment
terraform plan -var-file="prod.tfvars"

# Apply deployment
terraform apply -var-file="prod.tfvars"
```

## 🔐 Environment Variables

### Backend
| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:postgresql://localhost:5432/cms_db` |
| `SPRING_DATASOURCE_USERNAME` | DB Username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB Password | `postgres` |
| `JWT_SECRET` | JWT Secret Key | - |
| `JWT_EXPIRATION` | Token expiration (ms) | `3600000` |

### Frontend
| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_URL` | Backend API URL | `http://localhost:8080` |

## 📝 API Documentation

After starting the backend, access Swagger UI at:
- Local: http://localhost:8080/swagger-ui/index.html
- Production: https://api.your-domain.com/swagger-ui/index.html

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm run test
```

## 📄 License

This project is licensed under the MIT License.
