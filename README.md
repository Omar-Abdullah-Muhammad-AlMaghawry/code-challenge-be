# Code Challenge - Backend

A microservices-based Spring Boot backend providing JWT authentication, dashboard data, and stock market information for the Code Challenge application.

## Architecture

```
Frontend (4200)
      ↓
nginx :3000  (API gateway)
  ├── /api/auth/**       → auth-service:8081
  ├── /api/dashboard/**  → dashboard-service:8082
  └── /api/stocks/**     → stock-service:8083
```

| Service            | Port | Database      | Responsibility                        |
|--------------------|------|---------------|---------------------------------------|
| `auth-service`     | 8081 | Oracle (DEMO) | User registration, login, JWT issuing |
| `dashboard-service`| 8082 | None          | Stats and activity feed               |
| `stock-service`    | 8083 | None          | EGX + USA stock data                  |

JWT is validated independently in each service using the shared secret — no inter-service calls required.

## Tech Stack

- **Java 17**
- **Spring Boot 3.5** — one app per service
- **Spring Security** — stateless JWT filter chain in each service
- **Spring Data JPA** — auth-service only
- **Oracle Database** — auth-service persistent storage (XEPDB1)
- **JJWT 0.12** — JWT generation and validation
- **nginx** — API gateway and reverse proxy
- **Docker / docker-compose** — containerised local and CI deployment
- **Lombok** — boilerplate reduction

## API Endpoints

All routes go through nginx on port **3000**.

### Auth — `/api/auth`

| Method | Path        | Description              | Auth required |
|--------|-------------|--------------------------|---------------|
| POST   | `/login`    | Authenticate and get JWT | No            |
| POST   | `/register` | Register a new user      | No            |

### Dashboard — `/api/dashboard`

| Method | Path        | Description                   | Auth required |
|--------|-------------|-------------------------------|---------------|
| GET    | `/stats`    | Retrieve summary statistics   | Yes           |
| GET    | `/activity` | Retrieve recent activity feed | Yes           |

### Stocks — `/api/stocks`

| Method | Path | Description                                              | Auth required |
|--------|------|----------------------------------------------------------|---------------|
| GET    | `/`  | List stocks sorted by 24h change % (highest profit first)| Yes           |

**Query parameters:**

| Param    | Example          | Effect                          |
|----------|------------------|---------------------------------|
| `search` | `?search=apple`  | Filter by symbol or name        |
| `market` | `?market=EGX`    | `EGX`, `USA`, or `ALL` (default)|

Stock data: USA stocks are fetched live from Finnhub (5-min cache); EGX stocks (15 EGX30 symbols) are mocked — no free real-time EGX API exists.

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker and docker-compose
- Oracle Database (XE or higher) on `localhost:1521`, service name `XEPDB1`

### Database Setup

```sql
CREATE USER DEMO IDENTIFIED BY demo12;
GRANT CONNECT, RESOURCE TO DEMO;
```

### Run with Docker

```bash
docker-compose up --build
```

All services start and nginx listens on port **3000**.

### Run without Docker (development)

Start each service in a separate terminal:

```bash
./mvnw spring-boot:run -pl auth-service       # :8081
./mvnw spring-boot:run -pl dashboard-service  # :8082
./mvnw spring-boot:run -pl stock-service      # :8083
```

### Configuration

Each service has its own `application.properties`. Key properties:

**auth-service** (`auth-service/src/main/resources/application.properties`):
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=DEMO
spring.datasource.password=demo12
jwt.secret=<your-secret>
jwt.expiration=86400000
cors.allowed-origins=http://localhost:4200
```

**stock-service** (`stock-service/src/main/resources/application.properties`):
```properties
finnhub.api-key=        # get a free key at https://finnhub.io
jwt.secret=<your-secret>
```

`dashboard-service` only needs `jwt.secret`, `jwt.expiration`, and `cors.allowed-origins`.

## Test

Run all tests across all services:

```bash
./mvnw test
```

Run only unit tests:

```bash
./mvnw test -Dtest="*Test"
```

Run only integration tests:

```bash
./mvnw test -Dtest="*IT"
```

Tests use an in-memory H2 database (Oracle compatibility mode) for auth-service — no Oracle instance required for CI. dashboard-service and stock-service have no DB dependency at all.

## Project Structure

```
code-challenge-be/
├── auth-service/               # Auth, User entity, Oracle DB
│   └── src/main/java/com/example/auth/
│       ├── controller/         # AuthController
│       ├── dto/                # LoginRequest, RegisterRequest, LoginResponse
│       ├── entity/             # User (JPA)
│       ├── repository/         # UserRepository
│       ├── security/           # JwtUtil, JwtAuthFilter, SecurityConfig
│       └── service/            # AuthService, UserService
├── dashboard-service/          # Dashboard data, no DB
│   └── src/main/java/com/example/dashboard/
│       ├── controller/         # DashboardController
│       ├── dto/                # StatResponse, ActivityResponse
│       ├── security/           # JwtUtil, JwtAuthFilter, SecurityConfig
│       └── service/            # DashboardService
├── stock-service/              # EGX + USA stocks, no DB
│   └── src/main/java/com/example/stock/
│       ├── controller/         # StockController
│       ├── dto/                # StockResponse
│       ├── security/           # JwtUtil, JwtAuthFilter, SecurityConfig
│       └── service/            # StockService (Finnhub + EGX mock)
├── nginx/
│   └── nginx.conf              # Gateway routing config
├── docker-compose.yml          # Orchestrates all services + nginx
└── pom.xml                     # Multi-module parent POM
```
