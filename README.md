# Promax Workout Tracker API

A comprehensive Spring Boot-based REST API for tracking and managing workout data from Promax devices. This application provides user management, workout record tracking, goal setting, and progress monitoring with Redis caching optimization.

## Features

- **User Management**
  - User registration and authentication
  - JWT-based secure authentication
  - Password encryption with BCrypt

- **Workout Tracking**
  - Upload and manage workout records
  - Support for multiple workout types (Running, Walking, Hiking, Cycling)
  - Track duration, distance, calories burned, and notes
  - Paginated workout history
  - Workout summaries by type

- **Goal Management**
  - Set daily, weekly, monthly, or yearly fitness goals
  - Track multiple metrics (count, distance, duration, calories)
  - Real-time progress updates
  - Automatic goal achievement detection

- **Performance Optimization**
  - Redis caching for frequently accessed data
  - Asynchronous event processing
  - Connection pooling for database and cache

- **API Documentation**
  - Interactive Swagger/OpenAPI documentation
  - Comprehensive endpoint descriptions

## Technology Stack

- **Backend Framework**: Spring Boot 3.5.6
- **Java Version**: 17
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Authentication**: JWT (JSON Web Tokens)
- **API Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## Project Structure

```
promax-workout-tracker/
├── src/
│   ├── main/
│   │   ├── java/com/promax/workout/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── enums/           # Enumeration types
│   │   │   ├── event/           # Event handling
│   │   │   ├── exception/       # Exception handlers
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── security/        # Security & JWT
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Unit tests
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## Prerequisites

- Java 17 or higher
- Maven 3.9+
- Docker & Docker Compose (for containerized deployment)
- PostgreSQL 15+ (if running locally)
- Redis 7+ (if running locally)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/promax-workout-tracker.git
cd promax-workout-tracker
```

### 2. Start Database and Cache (Using Docker Compose)

```bash
docker-compose up -d
```

This will start:
- PostgreSQL on port 5432
- Redis on port 6379

### 3. Configure Application

Edit `src/main/resources/application.properties` if needed. Default configuration:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/workout_tracker_db
spring.datasource.username=workout_user
spring.datasource.password=workout_password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Server
server.port=8080
server.servlet.context-path=/api
```

### 4. Build and Run

#### Using Maven

```bash
# Build the project
mvn clean package -DskipTests

# Run the application
java -jar target/workout-tracker-api-1.0.0.jar
```

#### Using Maven Spring Boot Plugin

```bash
mvn spring-boot:run
```

#### Using Docker (Full Stack)

Uncomment the `app` service in `docker-compose.yml` and run:

```bash
docker-compose up --build
```

### 5. Access the Application

- **API Base URL**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/v1/health
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Docs**: http://localhost:8080/api/api-docs

## API Endpoints

### User Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/v1/users/register` | Register new user | No |
| POST | `/v1/users/login` | User login | No |
| GET | `/v1/users/{userId}` | Get user info | Yes |
| GET | `/v1/users/checkEmail` | Check email availability | No |
| GET | `/v1/users/count` | Get total user count | Yes |

### Workout Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/v1/workouts/upload` | Upload workout record | Yes |
| GET | `/v1/workouts/{userId}` | Get all workouts | Yes |
| GET | `/v1/workouts/{userId}/paginated` | Get paginated workouts | Yes |
| GET | `/v1/workouts/{userId}/recent` | Get recent N workouts | Yes |
| GET | `/v1/workouts/detail/{workoutId}` | Get workout details | Yes |
| GET | `/v1/workouts/{userId}/summary/byType` | Get workout summary by type | Yes |
| PUT | `/v1/workouts/{workoutId}` | Update workout | Yes |
| DELETE | `/v1/workouts/{workoutId}` | Delete workout | Yes |

### Goal Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/v1/users/{userId}/goals` | Create new goal | Yes |
| GET | `/v1/users/{userId}/goals` | Get all goals (optional status filter) | Yes |

### Health Check

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/v1/health` | API health status | No |

## Authentication

The API uses JWT (JSON Web Token) for authentication. After successful login, include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

Token expires after 24 hours.

## Example Usage

### Register a New User

```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

### Upload Workout

```bash
curl -X POST http://localhost:8080/api/v1/workouts/upload?userId=1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d '{
    "workoutType": "RUNNING",
    "durationMinutes": 45,
    "distanceKm": 8.5,
    "caloriesBurned": 450,
    "notes": "Morning run in the park"
  }'
```

### Create a Goal

```bash
curl -X POST http://localhost:8080/api/v1/users/1/goals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d '{
    "workoutType": "RUNNING",
    "period": "WEEKLY",
    "metrics": [
      {
        "metric": "DISTANCE",
        "targetValue": 50.0
      },
      {
        "metric": "DURATION",
        "targetValue": 300
      }
    ]
  }'
```

## Testing

Run unit tests:

```bash
mvn test
```

Run tests with coverage:

```bash
mvn test jacoco:report
```

## Database Schema

### Users Table
- `id`: Primary key
- `username`: Unique username
- `email`: Unique email address
- `password`: Encrypted password
- `created_at`: Registration timestamp
- `updated_at`: Last update timestamp

### Workouts Table
- `id`: Primary key
- `user_id`: Foreign key to users
- `workout_type`: ENUM (RUNNING, WALKING, HIKING, CYCLING)
- `duration_minutes`: Workout duration
- `distance_km`: Distance covered
- `calories_burned`: Calories burned
- `notes`: Optional notes
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp

### Goals Table
- `id`: Primary key
- `user_id`: Foreign key to users
- `workout_type`: Target workout type
- `period`: ENUM (DAILY, WEEKLY, MONTHLY, YEARLY)
- `status`: ENUM (ACTIVE, ACHIEVED, EXPIRED, CANCELED)
- `start_time`: Goal start date
- `end_time`: Goal end date

### Goal Metric Progress Table
- `id`: Primary key
- `goal_id`: Foreign key to goals
- `metric`: ENUM (COUNT, DISTANCE, DURATION, CALORIES)
- `target_value`: Target value to achieve
- `progress_value`: Current progress
- `progress_percent`: Progress percentage
- `status`: Metric status

## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/workout_tracker_db
spring.jpa.hibernate.ddl-auto=update

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
spring.cache.redis.time-to-live=600000

# Logging
logging.level.com.promax.workout=DEBUG
```

## Docker Deployment

### Build Docker Image

```bash
docker build -t promax-workout-tracker:latest .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

This starts:
- PostgreSQL database
- Redis cache
- Spring Boot application (if uncommented in docker-compose.yml)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring Boot for the excellent framework
- PostgreSQL for reliable data storage
- Redis for high-performance caching
- Swagger for API documentation

## Support

For issues, questions, or contributions, please open an issue on GitHub or contact the team at team@promax-workout-tracker.com.