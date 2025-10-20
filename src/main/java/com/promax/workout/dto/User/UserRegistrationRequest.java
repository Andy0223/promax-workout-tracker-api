package com.promax.workout.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User Registration DTO
 * Data Transfer Object for receiving user registration requests
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
public class UserRegistrationRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username length must be between 3-50 characters")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password length must be at least 6 characters")
    private String password;

    // Default constructor
    public UserRegistrationRequest() {
    }

    // Constructor with parameters
    public UserRegistrationRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserRegistrationRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
