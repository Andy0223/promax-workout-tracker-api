package com.promax.workout.dto;

import com.promax.workout.enums.GoalMetric;

import jakarta.validation.constraints.Positive;

public class GoalMetricRequestDto {
    private GoalMetric metric;
    @Positive
    private double targetValue;

    public GoalMetric getMetric() {
        return metric;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setMetric(GoalMetric metric) {
        this.metric = metric;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }
}
