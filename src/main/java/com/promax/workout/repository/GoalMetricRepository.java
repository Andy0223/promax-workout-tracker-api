package com.promax.workout.repository;

import com.promax.workout.entity.GoalMetricProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalMetricRepository extends JpaRepository<GoalMetricProgress, Long> {
}
