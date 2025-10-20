package com.promax.workout.controller.v1;

import com.promax.workout.dto.StandardResponseDto;
import com.promax.workout.dto.User.UserLoginRequest;
import com.promax.workout.dto.User.UserRegistrationRequest;
import com.promax.workout.dto.User.UserResponse;
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
@RequestMapping("v1/users")
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
     * @param userRegistrationRequest Registration information
     * @return Registration result
     */
    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Create new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<StandardResponseDto<UserResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {

        User user = userService.registerUser(userRegistrationRequest);

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreatedAt(user.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseDto.success("User registration successful", userResponse));
    }

    /**
     * User login
     * 
     * @param userLoginRequest Login request
     * @return Login result
     */
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "User login verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Incorrect username or password")
    })
    public ResponseEntity<StandardResponseDto<Map<String, Object>>> loginUser(
            @RequestBody UserLoginRequest userLoginRequest) {

        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();

        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password cannot be empty");
        }

        User user = userService.loginUser(email, password);
        String token = jwtUtil.generateToken(user.getUsername());

        Map<String, Object> loginData = new HashMap<>();
        loginData.put("token", token);
        loginData.put("expiresIn", "24h");
        loginData.put("userId", user.getId());
        loginData.put("username", user.getUsername());

        return ResponseEntity.ok(StandardResponseDto.success("Login successful", loginData));
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
    public ResponseEntity<StandardResponseDto<UserResponse>> getUserInfo(
            @Parameter(description = "User ID") @PathVariable Long userId) {

        Optional<User> userOptional = userService.findUserById(userId);

        // Handle the case where the user is not found
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(StandardResponseDto.error("User not found"));
        }

        User user = userOptional.get(); // Unwrap the Optional safely after the check

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return ResponseEntity.ok(StandardResponseDto.success(userResponse));
    }

    /**
     * Check email availability
     * 
     * @param email Email address
     * @return Check result
     */
    @GetMapping("/checkEmail")
    @Operation(summary = "Check Email Availability")
    public ResponseEntity<StandardResponseDto<UserResponse>> checkEmail(
            @Parameter(description = "Email address") @RequestParam String email) {

        boolean exists = userService.isEmailExists(email);

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(email);

        String message = exists ? "Email already exists" : "Email available";
        return ResponseEntity.ok(StandardResponseDto.success(message, userResponse));
    }

    /**
     * Get total number of users
     * 
     * @return Total number of users
     */
    @GetMapping("/count")
    @Operation(summary = "Get User Count", description = "Get total number of registered users in the system")
    public ResponseEntity<StandardResponseDto<Map<String, Object>>> getUserCount() {
        long count = userService.getUserCount();

        Map<String, Object> countData = new HashMap<>();
        countData.put("totalUsers", count);

        return ResponseEntity.ok(StandardResponseDto.success("User count retrieved successfully", countData));
    }
}
