# Dockerfile for Promax Workout Tracker API

# First stage: Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (using Docker cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Second stage: Run stage
FROM eclipse-temurin:17-jre

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create application user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy jar file from build stage
COPY --from=build /app/target/workout-tracker-api-1.0.0.jar app.jar

# Change file owner
RUN chown appuser:appuser app.jar

# Switch to application user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
