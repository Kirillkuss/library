## 📋 Table of Contents

- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Deployment](#-deployment)
- [System Architecture](#-system-architecture)
- [Monitoring & Observability](#-monitoring--observability)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [CI/CD Pipeline](#-cicd-pipeline)
---

## ✨ Key Features

### 📚 Core Functionality
- **Complete Book Management**: CRUD operations, search by author/title, filtering
- **User Management**: Registration, roles (ADMIN/USER), profile management
- **Book Loans**: Borrow/return system with due dates and notifications
- **Advanced Search**: Filter by author, genre, availability, and book number

### 🔐 Security
- **JWT Authentication** with token-based security
- **Two-Factor Authentication (2FA)** using TOTP (Time-based One-Time Password)
- **Role-Based Access Control** (RBAC) with ADMIN and USER roles
- **BCrypt Password Encryption** with configurable strength
- **Session Management** with single session limit

### ⚡ Performance
- **Redis Caching** for frequently accessed data (books, users)
- **Connection Pooling** for database optimization
- **Lazy Loading** for improved performance
- **Response Compression** for faster payload delivery

### 📹 Video Streaming
- **RTSP/WebRTC Support** via Mediamtx integration
- **Live Camera Feeds** for library monitoring
- **Low-Latency Streaming** with WebRTC

### 📊 Monitoring & Observability
- **Real-time Metrics Collection** with Micrometer
- **Prometheus Integration** for metrics storage
- **Grafana Dashboards** with pre-built visualizations
- **Custom Business Metrics**:
  - API request counting and tracking
  - Method execution time monitoring
  - Cache hit/miss rates
  - Error tracking by type and method
- **Health Checks**: Liveness, Readiness, and Startup probes
- **Performance Alerts** for critical thresholds

### 🛠 Developer Experience
- **AOP-based Logging**: Automatic logging for controllers and services
- **Transaction Management**: Declarative transaction handling
- **Global Exception Handling**: Centralized error management
- **Swagger/OpenAPI**: Interactive API documentation
- **Lombok**: Reduced boilerplate code
- **Docker Support**: Ready-to-run containers

---

## 🛠 Technology Stack

| Category | Technologies | Version |
|----------|-------------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot, Spring MVC, Spring Data, Spring Security | 3.x |
| **Database** | PostgreSQL | 15+ |
| **Cache** | Redis | 7+ |
| **Monitoring** | Micrometer, Prometheus, Grafana | Latest |
| **Video Streaming** | Mediamtx (RTSP/WebRTC) | Latest |
| **API Documentation** | Swagger/OpenAPI | 3.x |
| **Containerization** | Docker, Docker Compose, Kubernetes | Latest |
| **Build Tool** | Maven | 3.9+ |
| **Testing** | JUnit 5, Mockito, RestAssured | Latest |
| **CI/CD** | Jenkins | 2.x |
| **Logging** | SLF4J, Logback | Latest |
| **Code Quality** | Lombok | 1.18+ |

---

### AOP Cross-Cutting Concerns
- **`@ExecuteEndpointLog`** - Log all API endpoint calls
- **`@ExecuteMethodLog`** - Detailed service method logging
- **`@TrackMetrics`** - Automatic performance metrics collection
- **`@HandleException`** - Centralized exception handling
- **`@Transactional`** - Database transaction management

---

## 📊 Monitoring & Observability

### 🔍 Metrics Collected

#### API Metrics (`library.api.*`)

```promql
# Request counters by endpoint and status
library_api_requests_total{method="BookController.getBooksByAuthor", status="success"}

# Execution time histogram with percentiles
library_api_duration_seconds{method="BookController.getBooksByAuthor", quantile="0.95"}

# Error tracking by type
library_api_errors_total{method="BookService.saveBook", error_type="IllegalArgumentException"}

