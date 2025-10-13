package com.promax.workout.dto;

public class WorkoutTypeSummaryDto {
    private String workoutType;
    private long totalCount;
    private double totalDistanceKm;
    private double totalDurationMinutes;
    private double totalCaloriesBurned;
    private double averagePace; // 分鐘 / 公里

    public WorkoutTypeSummaryDto() {
    }

    public WorkoutTypeSummaryDto(String workoutType, long totalCount,
            double totalDistanceKm, double totalDurationMinutes, double totalCaloriesBurned) {
        this.workoutType = workoutType;
        this.totalCount = totalCount;
        this.totalDistanceKm = totalDistanceKm;
        this.totalDurationMinutes = totalDurationMinutes;
        this.totalCaloriesBurned = totalCaloriesBurned;
        this.averagePace = totalDistanceKm > 0 ? totalDurationMinutes / totalDistanceKm : 0;
    }

    // Getter and Setter
    public String getWorkoutType() {
        return workoutType;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public double getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public double getAveragePace() {
        return averagePace;
    }
}
