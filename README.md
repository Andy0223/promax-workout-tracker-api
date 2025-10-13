# Promax Workout Tracker API (MVP)

A **Spring Boot–based workout tracking API** for managing and recording fitness data from  devices.  


## 🎯 项目愿景

Build a backend system that allows users to upload and query their personal workout records, demonstrating skills in **API design**, **database architecture**, and **system performance optimization**.

## ✨ MVP Features

- 👤 **User Registration / Login** – Simplified user authentication system  
- 🏃‍♂️ **Workout Upload** – RESTful API for submitting workout data  
- 📊 **Workout Query** – Supports pagination and Redis caching optimization  
- 📚 **Swagger Documentation** – Complete API documentation and interactive UI  
- 🧪 **Unit Testing** – JUnit 5 tests covering core business logic  
- 🐳 **Dockerized Deployment** – Runs with PostgreSQL + Redis + Spring Boot application stack  

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.2.0  
- **Java Version:** 17  
- **Build Tool:** Maven  
- **Database:** PostgreSQL (Production) + H2 (Testing)  
- **Cache:** Redis  
- **API Documentation:** Swagger / OpenAPI 3  
- **Containerization:** Docker + Docker Compose  
- **Testing:** JUnit 5 + Mockito  

## 🚀 Getting Started

### Environment Requirements

- Java 17 or higher  
- Maven 3.6 or higher  
- Docker and Docker Compose *(recommended)*

### Option 1: Docker Compose Deployment (Recommended)

1. Clone the project locally:
   ```bash
   cd "/Users/andy/Desktop/Promax Workout Tracker API"

2. Start all services:
```bash
docker-compose up -d
```

3. Access the application
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
- `GET /api/workouts/{userId}/paginated` - Retrieve paginated workout data
- `GET /api/workouts/{userId}/recent` - Get most recent workouts
- `GET /api/workouts/{userId}/stats` - Retrieve workout statistics
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

### User Login
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

## 📁 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com//workout/
│   │       ├── WorkoutTrackerApplication.java
│   │       ├── config/
│   │       │   ├── SwaggerConfig.java
│   │       │   └── RedisConfig.java
│   │       ├── controller/
│   │       │   ├── HealthController.java
│   │       │   ├── UserController.java
│   │       │   └── WorkoutController.java
│   │       ├── dto/
│   │       │   ├── UserRegistrationDto.java
│   │       │   └── WorkoutUploadDto.java
│   │       ├── entity/
│   │       │   ├── User.java
│   │       │   └── Workout.java
│   │       ├── repository/
│   │       │   ├── UserRepository.java
│   │       │   └── WorkoutRepository.java
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

## License

MIT License
