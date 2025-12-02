# 📰 Enterprise CMS API

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

Enterprise Content Management System - Backend API for internal news management.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Setup & Run](#-setup--run)
- [API Documentation](#-api-documentation)
- [Database Configuration](#-database-configuration)
- [Testing Credentials](#-testing-credentials)
- [Docker Deployment](#-docker-deployment)

---

## 🛠 Tech Stack

| Technology | Version | Description |
|------------|---------|-------------|
| Java | 21 (LTS) | Programming Language |
| Spring Boot | 4.0.0 | Application Framework |
| Spring Security | 7.0.0 | Authentication & Authorization |
| Spring Data JPA | 4.0.0 | Data Access Layer |
| PostgreSQL | 17 | Relational Database |
| JWT (jjwt) | 0.13.0 | Token-based Authentication |
| OpenAPI 3.0 | 3.0.0 | API Documentation |
| Maven | 3.9+ | Build Tool |
| Docker | 24+ | Containerization |

---

## ✨ Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (RBAC): `ADMIN`, `REPORTER`
- BCrypt password encryption

### Category Management
- CRUD operations for news categories
- Auto-generated URL-friendly slugs
- Duplicate name validation

### News Management
- Full CRUD operations
- Ownership-based access control
- Status workflow: `DRAFT` → `PUBLISHED` → `HIDDEN`
- Guest users see only published articles

### Admin Features
- Dashboard with statistics
- Article publishing approval
- Full content moderation

---

## 🚀 Setup & Run

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 17+

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/congdinh2008/spring-boot-cms.git
   cd spring-boot-cms
   ```

2. **Configure database**
   
   Create PostgreSQL database:
   ```sql
   CREATE DATABASE cms_dev_db;
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 📖 API Documentation

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints Overview

| Module | Method | Endpoint | Access |
|--------|--------|----------|--------|
| **Auth** | POST | `/api/auth/login` | Public |
| | POST | `/api/auth/register` | Public |
| **Categories** | GET | `/api/v1/categories` | Public |
| | POST | `/api/v1/categories` | ADMIN |
| | PUT | `/api/v1/categories/{id}` | ADMIN |
| | DELETE | `/api/v1/categories/{id}` | ADMIN |
| **News** | GET | `/api/v1/news` | Public (PUBLISHED only) |
| | GET | `/api/v1/news/{id}` | Public |
| | POST | `/api/v1/news` | REPORTER, ADMIN |
| | PUT | `/api/v1/news/{id}` | Author only |
| | DELETE | `/api/v1/news/{id}` | Author or ADMIN |
| **Admin** | GET | `/api/v1/admin/dashboard` | ADMIN |
| | PUT | `/api/v1/admin/news/{id}/publish` | ADMIN |

---

## 🗃 Database Configuration

### Default Configuration

| Property | Value |
|----------|-------|
| Host | `localhost` |
| Port | `5432` |
| Database | `cms_dev_db` |
| Username | `postgres` |
| Password | `postgres` |

### Configuration File
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cms_dev_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## 🔐 Testing Credentials

### Admin Account
| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `Admin@1234` |
| Role | `ROLE_ADMIN` |

### Reporter Account
| Field | Value |
|-------|-------|
| Username | `reporter` |
| Password | `Reporter@1234` |
| Role | `ROLE_REPORTER` |

### Quick Test with cURL

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@1234"}'
```

**Access protected endpoint:**
```bash
curl -X GET http://localhost:8080/api/v1/admin/dashboard \
  -H "Authorization: Bearer <your-token>"
```

---

## 🐳 Docker Deployment

### Prerequisites
- Docker 24+
- Docker Compose 2.20+

### Quick Start

1. **Build and run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

2. **Check status**
   ```bash
   docker-compose ps
   ```

3. **View logs**
   ```bash
   docker-compose logs -f app
   ```

4. **Stop services**
   ```bash
   docker-compose down
   ```

### Services

| Service | Port | Description |
|---------|------|-------------|
| `app` | 8080 | Spring Boot Application |
| `db` | 5432 | PostgreSQL Database |

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://db:5432/cms_dev_db` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | (configured) | JWT signing key |
| `JWT_EXPIRATION` | `3600000` | Token expiration (ms) |

### Build Image Only
```bash
docker build -t cms-app:latest .
```

---

## 📁 Project Structure

```
cms/
├── src/
│   └── main/
│       ├── java/com/congdinh/cms/
│       │   ├── config/          # Security, JWT, OpenAPI configs
│       │   ├── controllers/     # REST Controllers
│       │   ├── dtos/            # Data Transfer Objects
│       │   ├── entities/        # JPA Entities
│       │   ├── enums/           # Enumerations
│       │   ├── exceptions/      # Custom Exceptions
│       │   ├── repositories/    # Data Access Layer
│       │   ├── services/        # Business Logic
│       │   └── utils/           # Utility Classes
│       └── resources/
│           └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Cong Dinh**
- GitHub: [@congdinh2008](https://github.com/congdinh2008)
