package com.promax.workout.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * Workout Entity Class
 * Corresponds to the workouts table in the database
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@Entity
@Table(name = "workouts")
public class Workout implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Workout type cannot be empty")
    @Column(name = "workout_type", nullable = false)
    private String workoutType;

    @NotNull(message = "Duration cannot be empty")
    @Min(value = 1, message = "Duration must be greater than 0 minutes")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull(message = "Distance cannot be empty")
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @NotNull(message = "Calories burned cannot be empty")
    @Min(value = 0, message = "Calories burned cannot be negative")
    @Column(name = "calories_burned", nullable = false)
    private Integer caloriesBurned;

    @Column(name = "notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-one relationship: multiple workout records belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Default constructor
    public Workout() {
    }

    // Constructor with parameters
    public Workout(String workoutType, Integer durationMinutes, BigDecimal distanceKm,
            Integer caloriesBurned, String notes, User user) {
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.distanceKm = distanceKm;
        this.caloriesBurned = caloriesBurned;
        this.notes = notes;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", workoutType='" + workoutType + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", distanceKm=" + distanceKm +
                ", caloriesBurned=" + caloriesBurned +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}
