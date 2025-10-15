package com.promax.workout.dto;

import com.promax.workout.enums.GoalPeriod;
import com.promax.workout.enums.GoalStatus;
import com.promax.workout.enums.WorkoutType;

import java.time.LocalDateTime;
import java.util.List;

public class GoalResponseDto {

    private Long id;
    private Long userId;
    private WorkoutType workoutType;
    private GoalPeriod period;
    private GoalStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<GoalMetricProgressDto> metrics;

    // Nested metric DTO
    public static class GoalMetricProgressDto {
        private String metric;
        private double targetValue;
        private double progressValue;
        private double progressPercent;

        // Getters and setters
        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }

        public double getTargetValue() {
            return targetValue;
        }

        public void setTargetValue(double targetValue) {
            this.targetValue = targetValue;
        }

        public double getProgressValue() {
            return progressValue;
        }

        public void setProgressValue(double progressValue) {
            this.progressValue = progressValue;
        }

        public double getProgressPercent() {
            return progressPercent;
        }

        public void setProgressPercent(double progressPercent) {
            this.progressPercent = progressPercent;
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public GoalPeriod getPeriod() {
        return period;
    }

    public void setPeriod(GoalPeriod period) {
        this.period = period;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<GoalMetricProgressDto> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<GoalMetricProgressDto> metrics) {
        this.metrics = metrics;
    }
}
