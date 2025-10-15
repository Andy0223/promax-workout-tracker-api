package com.promax.workout.repository;

import com.promax.workout.entity.Goal;
import com.promax.workout.enums.GoalStatus;
import com.promax.workout.enums.WorkoutType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId")
    List<Goal> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.status = :status")
    List<Goal> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") GoalStatus status);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.status = 'ACTIVE'")
    List<Goal> findActiveGoalsByUserId(@Param("userId") Long userId);

    List<Goal> findByUserIdAndWorkoutTypeAndStatus(Long userId, WorkoutType workoutType, GoalStatus status);
}
