# Code Challenge - Backend

A RESTful Spring Boot backend providing JWT-based authentication and dashboard data for the Code Challenge application.

## Tech Stack

- **Java 17**
- **Spring Boot 3.5**
- **Spring Security** — route protection and authentication
- **Spring Data JPA** — ORM layer
- **Oracle Database** — persistent storage (XEPDB1)
- **JJWT 0.12** — JWT token generation and validation
- **Lombok** — boilerplate reduction

## API Endpoints

### Auth — `/api/auth`

| Method | Path        | Description              | Auth required |
|--------|-------------|--------------------------|---------------|
| POST   | `/login`    | Authenticate and get JWT | No            |
| POST   | `/register` | Register a new user      | No            |

### Dashboard — `/api/dashboard`

| Method | Path        | Description                    | Auth required |
|--------|-------------|--------------------------------|---------------|
| GET    | `/stats`    | Retrieve summary statistics    | Yes           |
| GET    | `/activity` | Retrieve recent activity feed  | Yes           |

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Oracle Database (XE or higher) running on `localhost:1521` with service name `XEPDB1`

### Database Setup

Create the user and grant privileges in Oracle:

```sql
CREATE USER DEMO IDENTIFIED BY demo12;
GRANT CONNECT, RESOURCE TO DEMO;
```

### Configuration

Application settings are in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=DEMO
spring.datasource.password=demo12
jwt.secret=<your-secret>
jwt.expiration=86400000
cors.allowed-origins=http://localhost:4200
```

### Run

```bash
./mvnw spring-boot:run
```

The server starts on port **3000**.

### Test

```bash
./mvnw test
```

Tests use an in-memory H2 database (Oracle compatibility mode) — no Oracle instance required for CI.

## Project Structure

```
src/main/java/com/example/codechallenge/
├── controller/       # REST controllers (Auth, Dashboard)
├── dto/              # Request and response DTOs
├── entity/           # JPA entities (User)
├── repository/       # Spring Data repositories
├── security/         # JWT filter, JWT util, Security config
└── service/          # Business logic (Auth, Dashboard, User)
```
