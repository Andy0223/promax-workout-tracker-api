package com.promax.workout.entity;

import com.promax.workout.enums.GoalMetric;
import com.promax.workout.enums.GoalStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "goal_metric_progress")
public class GoalMetricProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalMetric metric;

    @Column(nullable = false)
    private double targetValue;

    @Column(nullable = false)
    private double progressValue;

    @Column(nullable = false)
    private double progressPercent = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.ACTIVE;

    // ---------- Getters / Setters ----------
    public Long getId() {
        return id;
    }

    public GoalMetric getMetric() {
        return metric;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public double getProgressValue() {
        return progressValue;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setMetric(GoalMetric metric) {
        this.metric = metric;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public void setProgressValue(double progressValue) {
        this.progressValue = progressValue;
    }

    public void setProgressPercent(double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }
}
