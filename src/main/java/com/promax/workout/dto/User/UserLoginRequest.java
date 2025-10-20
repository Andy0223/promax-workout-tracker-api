package com.promax.workout.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User Login Request DTO
 * Data Transfer Object for receiving user Login requests
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
public class UserLoginRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password length must be at least 6 characters")
    private String password;

    // Default constructor
    public UserLoginRequest() {
    }

    // Constructor with parameters
    public UserLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
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
        return "UserLoginRequest{" +
                ", email='" + email + '\'' +
                '}';
    }
}
