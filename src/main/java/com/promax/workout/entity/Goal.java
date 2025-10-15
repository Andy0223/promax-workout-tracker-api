package com.promax.workout.entity;

import com.promax.workout.enums.GoalPeriod;
import com.promax.workout.enums.GoalStatus;
import com.promax.workout.enums.WorkoutType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutType workoutType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalPeriod period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "goal_id")
    private List<GoalMetricProgress> metrics = new ArrayList<>();

    // ---------- Utility ----------
    public void addMetric(GoalMetricProgress metric) {
        this.metrics.add(metric);
    }

    // ---------- Getters / Setters ----------
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public GoalPeriod getPeriod() {
        return period;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<GoalMetricProgress> getMetrics() {
        return metrics;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public void setPeriod(GoalPeriod period) {
        this.period = period;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setMetrics(List<GoalMetricProgress> metrics) {
        this.metrics = metrics;
    }
}
