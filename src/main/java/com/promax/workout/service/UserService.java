package com.promax.workout.service;

import com.promax.workout.dto.User.UserRegistrationRequest;
import com.promax.workout.entity.User;
import com.promax.workout.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * User Service Layer
 * Handles business logic related to users
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * User registration
     * 
     * @param userRegistrationRequest Registration information
     * @return Successfully registered user information
     * @throws IllegalArgumentException if username or email already exists
     */
    public User registerUser(UserRegistrationRequest userRegistrationRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(userRegistrationRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userRegistrationRequest.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userRegistrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userRegistrationRequest.getEmail());
        }

        // Create new user
        User user = new User(
                userRegistrationRequest.getUsername(),
                userRegistrationRequest.getEmail(),
                passwordEncoder.encode(userRegistrationRequest.getPassword()) // Note: In real projects, password should
        );

        return userRepository.save(user);
    }

    /**
     * User login verification
     * 
     * @param username Username
     * @param password Password
     * @return User information (if verification successful)
     * @throws IllegalArgumentException if username or password is incorrect
     */
    @Transactional(readOnly = true)
    public User loginUser(String email, String password) {
        // Check if the user exists
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Incorrect email or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect email or password");
        }

        return user;
    }

    /**
     * Find user by ID
     * 
     * @param userId User ID
     * @return User information (if exists)
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Find user by username
     * 
     * @param username Username
     * @return User information (if exists)
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by email
     * 
     * @param email Email address
     * @return User information (if exists)
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Check if username exists
     * 
     * @param username Username
     * @return Whether exists
     */
    @Transactional(readOnly = true)
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     * 
     * @param email Email address
     * @return Whether exists
     */
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get total number of users
     * 
     * @return Total number of users
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.countAllUsers();
    }

    /**
     * Update user information
     * 
     * @param userId      User ID
     * @param updatedUser Updated user information
     * @return Updated user information
     * @throws IllegalArgumentException if user does not exist
     */
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist: " + userId));

        // Update fields (preserve ID and creation time)
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        return userRepository.save(existingUser);
    }

    /**
     * Delete user
     * 
     * @param userId User ID
     * @throws IllegalArgumentException if user does not exist
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User does not exist: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
