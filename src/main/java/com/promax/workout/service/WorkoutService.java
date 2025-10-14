package com.promax.workout.service;

import com.promax.workout.dto.WorkoutTypeSummaryDto;
import com.promax.workout.dto.WorkoutUploadDto;
import com.promax.workout.entity.User;
import com.promax.workout.entity.Workout;
import com.promax.workout.enums.WorkoutType;
import com.promax.workout.event.WorkoutEvent;
import com.promax.workout.repository.WorkoutRepository;
import com.promax.workout.repository.UserRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Workout Service Layer
 * Handles business logic related to workout records, includes Redis caching
 * functionality
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@Service
@Transactional
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public WorkoutService(WorkoutRepository workoutRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Upload workout record
     * 
     * @param userId    User ID
     * @param uploadDto Workout record data
     * @return Saved workout record
     * @throws IllegalArgumentException if user does not exist
     */
    public Workout uploadWorkout(Long userId, WorkoutUploadDto uploadDto) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist: " + userId));

        // Create workout record
        Workout workout = new Workout(
                uploadDto.getWorkoutType(),
                uploadDto.getDurationMinutes(),
                uploadDto.getDistanceKm(),
                uploadDto.getCaloriesBurned(),
                uploadDto.getNotes(),
                user);

        Workout saved = workoutRepository.save(workout);

        eventPublisher.publishEvent(new WorkoutEvent(saved));
        System.out.println("[Event Published] WorkoutEvent triggered for userId=" + userId);

        return saved;
    }

    /**
     * Get all workout records for a user (with cache)
     * 
     * @param userId User ID
     * @return List of workout records
     */
    @Cacheable(value = "workouts", key = "#userId")
    @Transactional(readOnly = true)
    public List<Workout> getUserWorkouts(Long userId) {
        List<Workout> workouts = workoutRepository.findByUserIdOrderByCreatedAtDesc(userId);
        // Initialize lazy-loaded associations to avoid serialization issues
        workouts.forEach(workout -> {
            if (workout.getUser() != null) {
                workout.getUser().getId(); // Trigger lazy loading
            }
        });

        System.out.println("Workouts: " + workouts);

        return workouts;
    }

    /**
     * Get paginated workout records for a user (with cache)
     * 
     * @param userId User ID
     * @param page   Page number (starting from 0)
     * @param size   Page size
     * @return Paginated workout records
     */
    @Cacheable(value = "workouts", key = "#userId + '_page_' + #page + '_' + #size")
    @Transactional(readOnly = true)
    public Page<Workout> getUserWorkoutsPaginated(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return workoutRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get recent N workout records for a user (with cache)
     * 
     * @param userId User ID
     * @param limit  Limit number
     * @return List of recent workout records
     */
    @Cacheable(value = "workouts", key = "#userId + '_recent_' + #limit")
    @Transactional(readOnly = true)
    public List<Workout> getRecentWorkouts(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return workoutRepository.findRecentWorkoutsByUserId(userId, pageable);
    }

    /**
     * Get workout records by workout type for a user (with cache)
     * 
     * @param userId      User ID
     * @param workoutType Workout type
     * @return List of workout records
     */
    @Cacheable(value = "workouts", key = "#userId + '_type_' + #workoutType")
    @Transactional(readOnly = true)
    public List<Workout> getUserWorkoutsByType(Long userId, String workoutType) {
        return workoutRepository.findByUserIdAndWorkoutTypeOrderByCreatedAtDesc(userId, workoutType);
    }

    /**
     * Get workout records by date range for a user (with cache)
     * 
     * @param userId    User ID
     * @param startDate Start date
     * @param endDate   End date
     * @return List of workout records
     */
    @Cacheable(value = "workouts", key = "#userId + '_range_' + #startDate + '_' + #endDate")
    @Transactional(readOnly = true)
    public List<Workout> getUserWorkoutsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return workoutRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Get workout record by ID
     * 
     * @param workoutId Workout ID
     * @return Workout record (if exists)
     */
    @Transactional(readOnly = true)
    public Optional<Workout> getWorkoutById(Long workoutId) {
        return workoutRepository.findById(workoutId);
    }

    /**
     * Update workout record
     * 
     * @param workoutId Workout ID
     * @param uploadDto Updated workout data
     * @return Updated workout record
     * @throws IllegalArgumentException if workout record does not exist
     */
    public Workout updateWorkout(Long workoutId, WorkoutUploadDto uploadDto) {
        Workout existingWorkout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("Workout record does not exist: " + workoutId));

        // Update fields
        existingWorkout.setWorkoutType(uploadDto.getWorkoutType());
        existingWorkout.setDurationMinutes(uploadDto.getDurationMinutes());
        existingWorkout.setDistanceKm(uploadDto.getDistanceKm());
        existingWorkout.setCaloriesBurned(uploadDto.getCaloriesBurned());
        existingWorkout.setNotes(uploadDto.getNotes());

        return workoutRepository.save(existingWorkout);
    }

    /**
     * 
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = "summaryByType", key = "#userId")
    @Transactional(readOnly = true)
    public List<WorkoutTypeSummaryDto> getUserSummaryByType(Long userId) {
        List<Object[]> rows = workoutRepository.getWorkoutSummaryByType(userId);
        List<WorkoutTypeSummaryDto> list = new ArrayList<>();
        for (Object[] r : rows) {
            WorkoutType type = (WorkoutType) r[0];
            long cnt = ((Number) r[1]).longValue();
            double dist = ((Number) r[2]).doubleValue();
            double dur = ((Number) r[3]).doubleValue();
            double cal = ((Number) r[4]).doubleValue();
            list.add(new WorkoutTypeSummaryDto(type, cnt, dist, dur, cal));
        }
        return list;
    }

    /**
     * 
     * @param userId
     * @return
     */
    @CachePut(cacheNames = "summaryByType", key = "#userId")
    public List<WorkoutTypeSummaryDto> refreshUserSummaryByType(Long userId) {
        return getUserSummaryByType(userId);
    }

    /**
     * Delete workout record
     * 
     * @param workoutId Workout ID
     * @throws IllegalArgumentException if workout record does not exist
     */
    public void deleteWorkout(Long workoutId) {
        if (!workoutRepository.existsById(workoutId)) {
            throw new IllegalArgumentException("Workout record does not exist: " + workoutId);
        }
        workoutRepository.deleteById(workoutId);
    }
}
