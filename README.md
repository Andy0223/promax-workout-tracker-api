# 🏋️‍♂️ Promax Workout Tracker API

A **Spring Boot 3.5** RESTful API for managing and recording personal workout data.  
Built to simulate Garmin’s backend data flow, featuring **JWT authentication**, **Redis caching**, **asynchronous event processing**, and **Dockerized deployment**.

---

## 🎯 Project Vision

Promax Workout Tracker API is designed to demonstrate backend development skills in:

- REST API design & implementation  
- Database schema design (PostgreSQL + JPA/Hibernate)  
- Redis caching and performance optimization  
- Asynchronous event-driven architecture  
- Containerization with Docker and Docker Compose  

---

## ✨ Core Features

### 👤 User Management
- User registration & login with encrypted credentials  
- JWT-based authentication & authorization  
- Validation and exception handling for input data  

### 🏃 Workout Tracking
- Upload and manage personal workout records  
- Retrieve workouts by user, workout type, or date range  
- Support for pagination and filtering  
- Automatic calculation of user-specific workout summaries (distance, duration, calories, pace)  

### ⚙️ Performance & Architecture
- Redis caching for faster queries  
- Async processing using `@Async` and event listeners for background computations  
- Custom cache key strategy for multi-level user statistics  

### 🧪 Quality & Documentation
- Comprehensive Swagger / OpenAPI 3 documentation  
- Unit tests using **JUnit 5** and **Mockito**  
- Consistent code style and meaningful API responses  

### 🐳 Deployment
- Fully containerized with Docker  
- Managed with Docker Compose (PostgreSQL + Redis + API)  
- Supports both local development and production builds  

---

## 🛠️ Tech Stack

| Category | Technology |
|-----------|-------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.0 |
| **Build Tool** | Maven |
| **Database** | PostgreSQL (prod), H2 (test) |
| **Cache** | Redis |
| **Security** | Spring Security + JWT |
| **Testing** | JUnit 5, Mockito |
| **Containerization** | Docker + Docker Compose |
| **Documentation** | Swagger / OpenAPI 3 |

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java **17+**
- Maven **3.6+**
- Docker & Docker Compose

---

### Option 1 — Run with Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/your-username/promax-workout-tracker-api.git
cd promax-workout-tracker-api

# Start all services
docker-compose up -d

Access the application
- Base API URL: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Health Check: http://localhost:8080/api/health

### Option 2: Local Development Setup

1. Start PostgreSQL and Redis
```bash
# Start database services using Docker
docker-compose up -d postgres redis
```

2. Compile and run the application
```bash
mvn clean compile
mvn spring-boot:run
```

3. Run tests
```bash
mvn test
```

## 📡 API Endpoints

### Healcheck

- `GET /api/health` - Retrieve service status

### User

- `POST /api/users/register` - User Registration
- `POST /api/users/login` - User login
- `GET /api/users/{userId}` - Retrieve User Info
- `GET /api/users/check-username` - Check username availability
- `GET /api/users/check-email` - Check email avalability
- `GET /api/users/count` - Get user account amounts

### Workout

- `POST /api/workouts/upload` - Upload workout record
- `GET /api/workouts/{userId}` - Retrieve user workouts (with cache)
- `GET /api/workouts/{userId}/paginated?page=0&size=10` - Paginated workout list
- `GET /api/workouts/{userId}/recent` - Get most recent workouts
- `/api/workouts/{userId}/summary/byType` - Get workout summary grouped by type
- `GET /api/workouts/detail/{workoutId}` - Retrieve specific workout detail
- `PUT /api/workouts/{workoutId}` - Update workout record
- `DELETE /api/workouts/{workoutId}` - Delete workout record

## 📋 Example Requests

### User Registration
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "fullName": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### User Login (JWT)
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### Upload Workout
```bash
curl -X POST "http://localhost:8080/api/workouts/upload?userId=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d '{
    "workoutType": "Running",
    "durationMinutes": 30,
    "distanceKm": 5.0,
    "caloriesBurned": 300,
    "notes": "Morning run record"
  }'
```

### Get User Workouts
```bash
curl http://localhost:8080/api/workouts/1
```

### Get User recent Workouts
```bash
curl http://localhost:8080/api/workouts/1/recent
```

### Get User's Workouts summary by type
```bash
curl -H "Authorization: Bearer <your-token>" \
  http://localhost:8080/api/workouts/1/summary/by-type
```

## 📁 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com//workout/
│   │       ├── WorkoutTrackerApplication.java
│   │       ├── config/
│   │       │   ├── AsyncConfig.java
│   │       │   └── RedisConfig.java
│   │       │   └── SecurityConfig.java
│   │       │   └── SwaggerConfig.java
│   │       ├── controller/
│   │       │   ├── HealthController.java
│   │       │   ├── UserController.java
│   │       │   └── WorkoutController.java
│   │       ├── dto/
│   │       │   ├── UserRegistrationDto.java
│   │       │   └── WorkoutTypeSummaryDto.java
│   │       │   └── WorkoutUploadDto.java
│   │       ├── entity/
│   │       │   ├── User.java
│   │       │   └── Workout.java
│   │       ├── event/
│   │       │   ├── WorkoutEvent.java
│   │       │   └── WorkoutEventListener.java
│   │       ├── repository/
│   │       │   ├── UserRepository.java
│   │       │   └── WorkoutRepository.java
│   │       ├── security/
│   │       │   ├── JwtAuthFilter.java
│   │       │   └── JetService.java
│   │       │   └── JwtUtil.java
│   │       └── service/
│   │           ├── UserService.java
│   │           └── WorkoutService.java
│   └── resources/
│       └── application.properties
├── test/
│   └── java/
│       └── com//workout/
│           └── service/
│               ├── UserServiceTest.java
│               └── WorkoutServiceTest.java
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## 🔧 Development Notes

- **Database**: PostgreSQL (Production) + H2 (Testing)
- **Cache**: Redis for query performance optimization
- **API Documentation**: Auto-generated Swagger / OpenAPI 3
- **Testing**: JUnit 5 + Mockito for unit testing
- **Containerization**: Docker + Docker Compose for one-click deployment
- **Logging**: DEBUG level for development
- **Hot Reload**: Enabled via Spring Boot DevTools

## Commands Overview

| Purpose          | Command                         |
| ---------------- | ------------------------------- |
| Build JAR        | `mvn clean package -DskipTests` |
| Run App          | `mvn spring-boot:run`           |
| Start via Docker | `docker-compose up -d`          |
| Stop Containers  | `docker-compose down`           |
| Run Tests        | `mvn test`                      |


## License

This project is licensed under the MIT License — feel free to use, modify, and share.
