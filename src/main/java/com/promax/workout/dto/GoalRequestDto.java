package com.promax.workout.dto;

import com.promax.workout.enums.GoalPeriod;
import com.promax.workout.enums.WorkoutType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class GoalRequestDto {

    @NotNull
    private WorkoutType workoutType;

    @NotNull
    private GoalPeriod period;

    @Size(min = 1, message = "At least one metric is required")
    private List<GoalMetricRequestDto> metrics;

    // --- getter/setter ---
    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public GoalPeriod getPeriod() {
        return period;
    }

    public List<GoalMetricRequestDto> getMetrics() {
        return metrics;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public void setPeriod(GoalPeriod period) {
        this.period = period;
    }

    public void setMetrics(List<GoalMetricRequestDto> metrics) {
        this.metrics = metrics;
    }
}
