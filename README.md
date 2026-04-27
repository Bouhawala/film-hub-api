# FilmHub API

A Spring Boot backend service that acts as a secure wrapper around the FilmHub external API, implementing JWT-based authentication and various enterprise-grade features.

## 🏗️ Architecture
```
┌─────────────────────────────────────────┐
│           Spring Boot (Modulith)        │
├─────────────┬───────────────────────────┤
│ Auth Module │ Films Module              │
│ - JWT Auth  │ - FilmHub API Proxy       │
│ - Register  │ - Caching (Caffeine)      │
│ - Login     │ - Rate Limiting           │
│ - Profile   │ - Resilience4j            │
└─────────────┴───────────────────────────┘
↕                    ↕
H2     FilmHub External API
https://api.film-hub.neofacto.dev
```

## 🚀 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Runtime |
| Spring Boot | 3.5.14 | Framework |
| Spring Modulith | 2.0.6 | Modular architecture |
| Spring Cloud OpenFeign | 2025.0.2 | HTTP client for FilmHub API |
| Spring Security | - | JWT authentication |
| Spring Data JPA | - | Database access |
| Flyway | - | Database migrations |
| Caffeine | - | In-memory caching |
| Resilience4j | 2.2.0 | Rate limiting |
| Springdoc OpenAPI | 2.8.17 | Swagger documentation |
| jjwt | 0.12.6 | JWT token generation |
| Logstash Logback | 8.0 | Structured logging |
| H2 | - | Local/test database |
| WireMock | 3.2.0 | Integration testing |

## 📦 Modules

| Module | Description |
|--------|-------------|
| `auth` | JWT authentication, user registration, login and profile update |
| `films` | FilmHub API proxy with caching and rate limiting |
| `shared` | CORS config, global exception handling, MDC logging |

## 🔐 API Endpoints

### Public
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | Login and get JWT token |
| `GET` | `/films` | Get all films |

### Protected (JWT Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/films/{id}` | Get film details |
| `PUT` | `/auth/account` | Update user profile |

## 🌍 Profiles

| Profile | Database | Security | Purpose |
|---------|---------|----------|---------|
| `local` | H2 | Disabled | Local development |
| `test` | H2 | Disabled | Testing |
| default | H2 | Enabled | Production |

## ⚙️ Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `JWT_SECRET` | JWT signing secret | Yes (prod) |
| `DB_URL` | No (defaults to H2) |
| `DB_USERNAME` | Database username | No |
| `DB_PASSWORD` | Database password | No |

## 🏃 Running Locally

### Prerequisites
- Java 21
- Maven 3.8+

### Steps

**1. Clone the repository:**
```bash
git clone <repository-url>
cd film-hub-api
```

**2. Run with local profile (H2, no security):**
```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

**3. Run with default profile (H2, full security):**
```bash
mvn spring-boot:run
```

### Useful URLs

| URL | Description |
|-----|-------------|
| `http://localhost:8080/swagger-ui/index.html` | Swagger UI |
| `http://localhost:8080/h2-console` | H2 Console (local only) |
| `http://localhost:8080/actuator/health` | Health check |
| `http://localhost:8080/actuator/info` | App info & version |

## 🧪 Running Tests

```bash
mvn test
```

Tests include:
- **Unit tests** — services and components in isolation
- **Slice tests** — MockMvc with mocked dependencies
- **Integration tests** — WireMock for real HTTP-level testing
- **Modulith test** — verifies module boundary compliance

## 🏛️ Technical Choices

| Choice | Reason |
|--------|--------|
| **Spring Modulith** | Enforces module boundaries, prevents tight coupling, easy to extract to microservices later |
| **JWT** | Stateless authentication, no session management needed |
| **OpenFeign** | Declarative HTTP client, minimal boilerplate for proxying FilmHub API |
| **Caffeine** | Fastest JVM cache for single-instance applications |
| **Resilience4j** | Rate limiting to protect against API abuse |
| **Flyway** | Version-controlled database migrations |
| **WireMock** | Real HTTP-level integration testing without mocking Feign client |
| **MDC Logging** | Structured logs with requestId for better observability |