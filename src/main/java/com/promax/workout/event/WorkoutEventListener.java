package com.promax.workout.event;

import com.promax.workout.service.WorkoutService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WorkoutEventListener {

    private final WorkoutService workoutService;

    public WorkoutEventListener(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @Async("asyncExecutor")
    @EventListener
    public void handleWorkoutEvent(WorkoutEvent event) {
        Long userId = event.getWorkout().getUser().getId();
        System.out.println("[Async] Recalculating summaries for userId=" + userId);
        workoutService.refreshUserSummaryByType(userId);
        System.out.println("[Async] Summaries refreshed for userId=" + userId);
    }
}