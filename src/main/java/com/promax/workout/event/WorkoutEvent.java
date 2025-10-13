package com.promax.workout.event;

import com.promax.workout.entity.Workout;

public class WorkoutEvent {

    private final Workout workout;

    public WorkoutEvent(Workout workout) {
        this.workout = workout;
    }

    public Workout getWorkout() {
        return workout;
    }
}
