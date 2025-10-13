package com.promax.workout.controller;

import com.promax.workout.dto.UserRegistrationDto;
import com.promax.workout.entity.User;
import com.promax.workout.service.UserService;
import com.promax.workout.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * User Controller
 * Handles API requests related to user registration and login
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User registration and login related APIs")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * User registration
     * 
     * @param registrationDto Registration information
     * @return Registration result
     */
    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Create new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(registrationDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registration successful");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * User login
     * 
     * @param loginRequest Login request
     * @return Login result
     */
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "User login verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Incorrect username or password")
    })
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            if (username == null || password == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Username and password cannot be empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            User user = userService.loginUser(username, password);
            String token = jwtUtil.generateToken(user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("expiresIn", "24h");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Get user information
     * 
     * @param userId User ID
     * @return User information
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get User Information", description = "Get user detailed information by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @Parameter(description = "User ID") @PathVariable Long userId) {

        Optional<User> userOptional = userService.findUserById(userId);

        if (userOptional.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("createdAt", user.getCreatedAt());
        response.put("updatedAt", user.getUpdatedAt());

        return ResponseEntity.ok(response);
    }

    /**
     * Check username availability
     * 
     * @param username Username
     * @return Check result
     */
    @GetMapping("/check-username")
    @Operation(summary = "Check Username Availability", description = "Check if username is already in use")
    public ResponseEntity<Map<String, Object>> checkUsername(
            @Parameter(description = "Username") @RequestParam String username) {

        boolean exists = userService.isUsernameExists(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("available", !exists);
        response.put("message", exists ? "Username already exists" : "Username available");

        return ResponseEntity.ok(response);
    }

    /**
     * Check email availability
     * 
     * @param email Email address
     * @return Check result
     */
    @GetMapping("/check-email")
    @Operation(summary = "Check Email Availability", description = "Check if email is already in use")
    public ResponseEntity<Map<String, Object>> checkEmail(
            @Parameter(description = "Email address") @RequestParam String email) {

        boolean exists = userService.isEmailExists(email);

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("available", !exists);
        response.put("message", exists ? "Email already exists" : "Email available");

        return ResponseEntity.ok(response);
    }

    /**
     * Get total number of users
     * 
     * @return Total number of users
     */
    @GetMapping("/count")
    @Operation(summary = "Get User Count", description = "Get total number of registered users in the system")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        long count = userService.getUserCount();

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", count);
        response.put("message", "User count retrieved successfully");

        return ResponseEntity.ok(response);
    }
}
