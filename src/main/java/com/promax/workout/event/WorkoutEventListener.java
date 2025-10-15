package com.promax.workout.event;

import com.promax.workout.service.WorkoutService;
import com.promax.workout.entity.Workout;
import com.promax.workout.service.GoalService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WorkoutEventListener {

    private final WorkoutService workoutService;
    private final GoalService goalService;

    public WorkoutEventListener(WorkoutService workoutService, GoalService goalService) {
        this.workoutService = workoutService;
        this.goalService = goalService;
    }

    @Async("asyncExecutor")
    @EventListener
    public void handleWorkoutEvent(WorkoutEvent event) {
        Workout workout = event.getWorkout();
        Long userId = workout.getUser().getId();

        System.out.println("[Async] Recalculating summaries for userId=" + userId);
        workoutService.refreshUserSummaryByType(userId);
        System.out.println("[Async] Summaries refreshed for userId=" + userId);
        goalService.updateGoalsForNewWorkout(workout);
    }
}