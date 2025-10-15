package com.promax.workout.controller.v1;

import com.promax.workout.dto.GoalRequestDto;
import com.promax.workout.dto.GoalResponseDto;
import com.promax.workout.entity.Goal;
import com.promax.workout.enums.GoalStatus;
import com.promax.workout.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users/{userId}/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @Operation(summary = "Create a new Goal")
    public ResponseEntity<GoalResponseDto> createGoal(@PathVariable Long userId, @RequestBody GoalRequestDto request) {
        var goal = goalService.createGoal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.toDto(goal));
    }

    @GetMapping
    @Operation(summary = "Get all goals (optionally filtered by status)")
    public ResponseEntity<List<GoalResponseDto>> getGoals(
            @PathVariable Long userId,
            @RequestParam(value = "status", required = false) GoalStatus status) {
        List<GoalResponseDto> goals = goalService.getGoalsByUser(userId, status);
        return ResponseEntity.ok(goals);
    }

    // @DeleteMapping("/{goalId}")
    // @Operation(summary = "Cancel a goal")
    // public ResponseEntity<Void> cancel(@PathVariable Long userId, @PathVariable
    // Long goalId) {
    // goalService.cancelGoal(userId, goalId);
    // return ResponseEntity.noContent().build();
    // }
}
