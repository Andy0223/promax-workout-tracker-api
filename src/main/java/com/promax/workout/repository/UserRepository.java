package com.promax.workout.repository;

import com.promax.workout.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Data Access Layer
 * Provides database operations related to users
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * 
     * @param username Username
     * @return User information (if exists)
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * 
     * @param email Email address
     * @return User information (if exists)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     * 
     * @param username Username
     * @return Whether exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * 
     * @param email Email address
     * @return Whether exists
     */
    boolean existsByEmail(String email);

    /**
     * Find user by username and password (for login verification)
     * 
     * @param username Username
     * @param password Password
     * @return User information (if exists)
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
    Optional<User> findByEmailAndPassword(@Param("email") String email,
            @Param("password") String password);

    /**
     * Count total number of users
     * 
     * @return Total number of users
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
}
