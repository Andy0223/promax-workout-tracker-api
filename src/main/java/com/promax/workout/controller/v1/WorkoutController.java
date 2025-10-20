package com.promax.workout.controller.v1;

import com.promax.workout.dto.StandardResponseDto;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1/workouts")
@Tag(name = "Workout Management", description = "Workout record upload and query related APIs")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService, ApplicationEventPublisher eventPublisher) {
        this.workoutService = workoutService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload workout record", description = "User uploads new workout record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Upload successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<StandardResponseDto<Map<String, Object>>> uploadWorkout(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Valid @RequestBody WorkoutUploadDto uploadDto) {

        Workout workout = workoutService.uploadWorkout(userId, uploadDto);

        Map<String, Object> data = new HashMap<>();
        data.put("workoutId", workout.getId());
        data.put("workoutType", workout.getWorkoutType());
        data.put("durationMinutes", workout.getDurationMinutes());
        data.put("distanceKm", workout.getDistanceKm());
        data.put("caloriesBurned", workout.getCaloriesBurned());
        data.put("createdAt", workout.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseDto.success("Workout uploaded successfully", data));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user's workout records")
    public ResponseEntity<StandardResponseDto<Map<String, Object>>> getUserWorkouts(
            @PathVariable Long userId) {

        List<Workout> workouts = workoutService.getUserWorkouts(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("totalWorkouts", workouts.size());
        data.put("workouts", workouts);

        return ResponseEntity.ok(StandardResponseDto.success("Workouts retrieved successfully", data));
    }

    @GetMapping("/{userId}/paginated")
    @Operation(summary = "Get paginated user's workout records")
    public ResponseEntity<StandardResponseDto<Map<String, Object>>> getUserWorkoutsPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Workout> workoutPage = workoutService.getUserWorkoutsPaginated(userId, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("currentPage", workoutPage.getNumber());
        data.put("totalPages", workoutPage.getTotalPages());
        data.put("totalElements", workoutPage.getTotalElements());
        data.put("workouts", workoutPage.getContent());

        return ResponseEntity.ok(StandardResponseDto.success("Paginated workouts retrieved", data));
    }

    @GetMapping("/{userId}/recent")
    @Operation(summary = "Get recent N workout records for a user")
    public ResponseEntity<StandardResponseDto<Map<String, Object>>> getRecentWorkouts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {

        List<Workout> workouts = workoutService.getRecentWorkouts(userId, limit);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("limit", limit);
        data.put("workouts", workouts);

        return ResponseEntity.ok(StandardResponseDto.success("Recent workouts retrieved", data));
    }

    @GetMapping("/detail/{workoutId}")
    @Operation(summary = "Get workout record details")
    public ResponseEntity<StandardResponseDto<Map<String, Object>>> getWorkoutById(
            @PathVariable Long workoutId) {

        Workout workout = workoutService.getWorkoutById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("Workout record does not exist"));

        Map<String, Object> data = new HashMap<>();
        data.put("workoutId", workout.getId());
        data.put("workoutType", workout.getWorkoutType());
        data.put("durationMinutes", workout.getDurationMinutes());
        data.put("distanceKm", workout.getDistanceKm());
        data.put("caloriesBurned", workout.getCaloriesBurned());
        data.put("notes", workout.getNotes());
        data.put("createdAt", workout.getCreatedAt());
        data.put("updatedAt", workout.getUpdatedAt());
        data.put("userId", workout.getUser().getId());

        return ResponseEntity.ok(StandardResponseDto.success("Workout retrieved successfully", data));
    }

    @PutMapping("/{workoutId}")
    @Operation(summary = "Update workout record")
    public ResponseEntity<StandardResponseDto<Map<String, Object>>> updateWorkout(
            @PathVariable Long workoutId,
            @Valid @RequestBody WorkoutUploadDto uploadDto) {

        Workout workout = workoutService.updateWorkout(workoutId, uploadDto);

        Map<String, Object> data = new HashMap<>();
        data.put("workoutId", workout.getId());
        data.put("workoutType", workout.getWorkoutType());
        data.put("durationMinutes", workout.getDurationMinutes());
        data.put("distanceKm", workout.getDistanceKm());
        data.put("caloriesBurned", workout.getCaloriesBurned());
        data.put("updatedAt", workout.getUpdatedAt());

        return ResponseEntity.ok(StandardResponseDto.success("Workout updated successfully", data));
    }

    @GetMapping("/{userId}/summary/byType")
    @Operation(summary = "Get Workout Summary by Type")
    public ResponseEntity<StandardResponseDto<List<WorkoutTypeSummaryDto>>> getSummaryByType(
            @PathVariable Long userId) {

        List<WorkoutTypeSummaryDto> summary = workoutService.getUserSummaryByType(userId);
        return ResponseEntity.ok(StandardResponseDto.success("Workout summary retrieved", summary));
    }

    @DeleteMapping("/{workoutId}")
    @Operation(summary = "Delete workout record")
    public ResponseEntity<StandardResponseDto<Void>> deleteWorkout(@PathVariable Long workoutId) {
        workoutService.deleteWorkout(workoutId);
        return ResponseEntity.ok(StandardResponseDto.success("Workout deleted successfully", null));
    }
}
