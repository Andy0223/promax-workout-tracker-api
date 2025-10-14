package com.promax.workout.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

import com.promax.workout.enums.WorkoutType;

/**
 * Workout Upload DTO
 * Data Transfer Object for receiving workout upload requests
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
public class WorkoutUploadDto {

    @NotNull(message = "Workout type is required")
    private WorkoutType workoutType;

    @NotNull(message = "Duration cannot be empty")
    @Min(value = 1, message = "Duration must be greater than 0 minutes")
    private Integer durationMinutes;

    @NotNull(message = "Distance cannot be empty")
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    private BigDecimal distanceKm;

    @NotNull(message = "Calories burned cannot be empty")
    @Min(value = 0, message = "Calories burned cannot be negative")
    private Integer caloriesBurned;

    @Size(max = 500, message = "Notes length cannot exceed 500 characters")
    private String notes;

    // Default constructor
    public WorkoutUploadDto() {
    }

    // Constructor with parameters
    public WorkoutUploadDto(WorkoutType workoutType, Integer durationMinutes, BigDecimal distanceKm,
            Integer caloriesBurned, String notes) {
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.distanceKm = distanceKm;
        this.caloriesBurned = caloriesBurned;
        this.notes = notes;
    }

    // Getters and Setters
    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "WorkoutUploadDto{" +
                "workoutType='" + workoutType + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", distanceKm=" + distanceKm +
                ", caloriesBurned=" + caloriesBurned +
                ", notes='" + notes + '\'' +
                '}';
    }
}
