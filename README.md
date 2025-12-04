# CMS - Content Management System

A full-stack Content Management System built with Spring Boot backend and React TypeScript frontend.

## 🏗️ Project Structure

```
cms/
├── backend/                 # Spring Boot API
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── docker-compose.yml
├── frontend/                # React + TypeScript + Vite
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── infrastructure/          # Infrastructure as Code
│   ├── terraform/          # GCP Terraform configs
│   └── docker/             # Docker configurations
└── README.md
```

## 🚀 Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.0
- **Security**: Spring Security 7.0 + JWT
- **Database**: PostgreSQL 17
- **API Documentation**: OpenAPI 3.0 (Swagger)

### Frontend
- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite
- **State Management**: TanStack Query
- **UI Library**: Tailwind CSS + Shadcn/ui
- **Routing**: React Router v6

### Infrastructure
- **Cloud**: Google Cloud Platform (GCP)
- **Container**: Docker + Cloud Run
- **Database**: Cloud SQL (PostgreSQL)
- **IaC**: Terraform
- **CI/CD**: GitHub Actions

## 🛠️ Development Setup

### Prerequisites
- Java 21+
- Node.js 20+
- Docker & Docker Compose
- PostgreSQL 17 (or use Docker)

### Backend Development

```bash
cd backend

# Start database
docker-compose up -d postgres

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

# Start dev server
npm run dev

# App available at http://localhost:5173
```

### Full Stack (Docker Compose)

```bash
# From root directory
docker-compose -f infrastructure/docker/docker-compose.yml up -d

# Backend: http://localhost:8080
# Frontend: http://localhost:3000
```

## 📦 Deployment

### Deploy to GCP using Terraform

```bash
cd infrastructure/terraform

# Initialize Terraform
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
