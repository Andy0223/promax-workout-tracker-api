package com.promax.workout.repository;

import com.promax.workout.entity.Workout;
import com.promax.workout.enums.WorkoutType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Workout Data Access Layer
 * Provides database operations related to workout records
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    /**
     * Find all workout records by user ID
     * 
     * @param userId User ID
     * @return List of workout records
     */
    List<Workout> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find workout records by user ID with pagination
     * 
     * @param userId   User ID
     * @param pageable Pagination parameters
     * @return Paginated workout records
     */
    Page<Workout> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find workout records by user ID and workout type
     * 
     * @param userId      User ID
     * @param workoutType Workout type
     * @return List of workout records
     */
    List<Workout> findByUserIdAndWorkoutTypeOrderByCreatedAtDesc(Long userId, String workoutType);

    /**
     * Find workout records by user ID and date range
     * 
     * @param userId    User ID
     * @param startDate Start date
     * @param endDate   End date
     * @return List of workout records
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.createdAt BETWEEN :startDate AND :endDate ORDER BY w.createdAt DESC")
    List<Workout> findByUserIdAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Count total number of workouts for a user
     * 
     * @param userId User ID
     * @return Number of workouts
     */
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Calculate total workout duration in minutes for a user
     * 
     * @param userId User ID
     * @return Total workout duration
     */
    @Query("SELECT COALESCE(SUM(w.durationMinutes), 0) FROM Workout w WHERE w.user.id = :userId")
    long sumDurationByUserId(@Param("userId") Long userId);

    /**
     * Calculate total workout distance in kilometers for a user
     * 
     * @param userId User ID
     * @return Total workout distance
     */
    @Query("SELECT COALESCE(SUM(w.distanceKm), 0) FROM Workout w WHERE w.user.id = :userId")
    Double sumDistanceByUserId(@Param("userId") Long userId);

    /**
     * Calculate total calories burned for a user
     * 
     * @param userId User ID
     * @return Total calories burned
     */
    @Query("SELECT COALESCE(SUM(w.caloriesBurned), 0) FROM Workout w WHERE w.user.id = :userId")
    long sumCaloriesByUserId(@Param("userId") Long userId);

    /**
     * Find recent N workout records for a user
     * 
     * @param userId User ID
     * @param limit  Limit number
     * @return List of recent workout records
     */
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId ORDER BY w.createdAt DESC")
    List<Workout> findRecentWorkoutsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 
     * @param userId
     * @return
     */
    @Query("SELECT w.workoutType, COUNT(w), COALESCE(SUM(w.distanceKm), 0), " +
            "COALESCE(SUM(w.durationMinutes), 0), COALESCE(SUM(w.caloriesBurned), 0) " +
            "FROM Workout w WHERE w.user.id = :userId GROUP BY w.workoutType")
    List<Object[]> getWorkoutSummaryByType(@Param("userId") Long userId);

    @Query("""
            select count(w), coalesce(sum(w.distanceKm),0), coalesce(sum(w.durationMinutes),0), coalesce(sum(w.caloriesBurned),0)
            from Workout w
            where w.user.id = :userId
              and w.workoutType = :type
              and w.createdAt >= :start
              and w.createdAt <  :end
            """)
    Object aggregateForGoalPeriod(@Param("userId") Long userId,
            @Param("type") WorkoutType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
