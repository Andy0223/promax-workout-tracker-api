package com.promax.workout.controller;

import com.promax.workout.dto.WorkoutTypeSummaryDto;
import com.promax.workout.dto.WorkoutUploadDto;
import com.promax.workout.entity.Workout;
import com.promax.workout.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Workout Controller
 * Provides CRUD operations for workout data, supports Redis caching
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/workouts")
@Tag(name = "Workout Management", description = "Workout record upload and query related APIs")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    /**
     * Upload workout record
     * 
     * @param userId    User ID
     * @param uploadDto Workout record data
     * @return Upload result
     */
    @PostMapping("/upload")
    @Operation(summary = "Upload workout record", description = "User uploads new workout record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public ResponseEntity<Map<String, Object>> uploadWorkout(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Valid @RequestBody WorkoutUploadDto uploadDto) {

        try {
            Workout workout = workoutService.uploadWorkout(userId, uploadDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Exercise record uploaded successfully");
            response.put("workoutId", workout.getId());
            response.put("workoutType", workout.getWorkoutType());
            response.put("durationMinutes", workout.getDurationMinutes());
            response.put("distanceKm", workout.getDistanceKm());
            response.put("caloriesBurned", workout.getCaloriesBurned());
            response.put("createdAt", workout.getCreatedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get user's workout records
     * 
     * @param userId User ID
     * @return List of workout records
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user's workout records", description = "Get all workout records for a user (with cache)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get successful"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getUserWorkouts(
            @Parameter(description = "User ID") @PathVariable Long userId) {

        try {
            List<Workout> workouts = workoutService.getUserWorkouts(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("totalWorkouts", workouts.size());
            response.put("workouts", workouts);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Get workout records failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get paginated user's workout records
     * 
     * @param userId User ID
     * @param page   Page number (starting from 0)
     * @param size   Page size
     * @return Paginated workout records
     */
    @GetMapping("/{userId}/paginated")
    @Operation(summary = "Get paginated user's workout records", description = "Get paginated workout records for a user (with cache)")
    public ResponseEntity<Map<String, Object>> getUserWorkoutsPaginated(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number (starting from 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        Page<Workout> workoutPage = workoutService.getUserWorkoutsPaginated(userId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("currentPage", workoutPage.getNumber());
        response.put("totalPages", workoutPage.getTotalPages());
        response.put("totalElements", workoutPage.getTotalElements());
        response.put("size", workoutPage.getSize());
        response.put("workouts", workoutPage.getContent());

        return ResponseEntity.ok(response);
    }

    /**
     * Get recent N workout records for a user
     * 
     * @param userId User ID
     * @param limit  Limit number
     * @return List of recent workout records
     */
    @GetMapping("/{userId}/recent")
    @Operation(summary = "Get recent N workout records for a user", description = "Get recent N workout records for a user (with cache)")
    public ResponseEntity<Map<String, Object>> getRecentWorkouts(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Limit number") @RequestParam(defaultValue = "5") int limit) {

        List<Workout> workouts = workoutService.getRecentWorkouts(userId, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("limit", limit);
        response.put("workouts", workouts);

        return ResponseEntity.ok(response);
    }

    /**
     * Get workout record by ID
     * 
     * @param workoutId Workout ID
     * @return Workout record details
     */
    @GetMapping("/detail/{workoutId}")
    @Operation(summary = "Get workout record details", description = "Get workout record details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get successful"),
            @ApiResponse(responseCode = "404", description = "Workout record does not exist")
    })
    public ResponseEntity<Map<String, Object>> getWorkoutById(
            @Parameter(description = "Workout ID") @PathVariable Long workoutId) {

        Optional<Workout> workoutOptional = workoutService.getWorkoutById(workoutId);

        if (workoutOptional.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Workout record does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Workout workout = workoutOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("workoutId", workout.getId());
        response.put("workoutType", workout.getWorkoutType());
        response.put("durationMinutes", workout.getDurationMinutes());
        response.put("distanceKm", workout.getDistanceKm());
        response.put("caloriesBurned", workout.getCaloriesBurned());
        response.put("notes", workout.getNotes());
        response.put("createdAt", workout.getCreatedAt());
        response.put("updatedAt", workout.getUpdatedAt());
        response.put("userId", workout.getUser().getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Update workout record
     * 
     * @param workoutId Workout ID
     * @param uploadDto Updated workout data
     * @return Update result
     */
    @PutMapping("/{workoutId}")
    @Operation(summary = "Update workout record", description = "Update specified workout record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Workout record does not exist")
    })
    public ResponseEntity<Map<String, Object>> updateWorkout(
            @Parameter(description = "Workout ID") @PathVariable Long workoutId,
            @Valid @RequestBody WorkoutUploadDto uploadDto) {

        try {
            Workout workout = workoutService.updateWorkout(workoutId, uploadDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Workout record updated successfully");
            response.put("workoutId", workout.getId());
            response.put("workoutType", workout.getWorkoutType());
            response.put("durationMinutes", workout.getDurationMinutes());
            response.put("distanceKm", workout.getDistanceKm());
            response.put("caloriesBurned", workout.getCaloriesBurned());
            response.put("updatedAt", workout.getUpdatedAt());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{userId}/summary/byType")
    @Operation(summary = "Get Workout Summary by Type")
    public ResponseEntity<List<WorkoutTypeSummaryDto>> getSummaryByType(@PathVariable Long userId) {
        return ResponseEntity.ok(workoutService.getUserSummaryByType(userId));
    }

    /**
     * Delete workout record
     * 
     * @param workoutId Workout ID
     * @return Delete result
     */
    @DeleteMapping("/{workoutId}")
    @Operation(summary = "Delete workout record", description = "Delete specified workout record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete successful"),
            @ApiResponse(responseCode = "404", description = "Workout record does not exist")
    })
    public ResponseEntity<Map<String, Object>> deleteWorkout(
            @Parameter(description = "Workout ID") @PathVariable Long workoutId) {

        try {
            workoutService.deleteWorkout(workoutId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Workout record deleted successfully");

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}