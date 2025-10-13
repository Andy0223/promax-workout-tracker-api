package com.promax.workout.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration Class
 * Configures basic information for API documentation
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

        @Bean
        OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Promax Workout Tracker API")
                                                .description("""
                                                                A Spring Boot-based workout tracking API for managing and recording workout data from Promax devices.

                                                                ## Main Features
                                                                - User registration and login
                                                                - Workout record upload and query
                                                                - Redis caching optimization
                                                                - PostgreSQL database storage
                                                                - Swagger API documentation

                                                                ## Technology Stack
                                                                - Spring Boot 3.2.0
                                                                - Java 17
                                                                - PostgreSQL
                                                                - Redis
                                                                - Swagger/OpenAPI 3""")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Promax Workout Tracker Team")
                                                                .email("team@promax-workout-tracker.com")
                                                                .url("https://github.com/promax-workout-tracker"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8080/api")
                                                                .description("Development environment server"),
                                                new Server()
                                                                .url("https://api.promax-workout-tracker.com")
                                                                .description("Production environment server")));
        }
}
