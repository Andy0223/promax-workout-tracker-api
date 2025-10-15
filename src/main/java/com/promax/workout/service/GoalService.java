package com.promax.workout.service;

import com.promax.workout.dto.GoalRequestDto;
import com.promax.workout.dto.GoalResponseDto;
import com.promax.workout.entity.*;
import com.promax.workout.enums.*;
import com.promax.workout.repository.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMetricRepository goalMetricRepository;
    private final UserRepository userRepository;
    private final WorkoutService workoutService;

    public GoalService(GoalRepository goalRepository, GoalMetricRepository goalMetricRepository,
            UserRepository userRepository,
            WorkoutService workoutService) {
        this.goalRepository = goalRepository;
        this.goalMetricRepository = goalMetricRepository;
        this.userRepository = userRepository;
        this.workoutService = workoutService;
    }

    public GoalResponseDto toDto(Goal goal) {
        GoalResponseDto dto = new GoalResponseDto();
        dto.setId(goal.getId());
        dto.setUserId(goal.getUser().getId());
        dto.setWorkoutType(goal.getWorkoutType());
        dto.setPeriod(goal.getPeriod());
        dto.setStatus(goal.getStatus());
        dto.setStartTime(goal.getStartTime());
        dto.setEndTime(goal.getEndTime());

        List<GoalResponseDto.GoalMetricProgressDto> metrics = goal.getMetrics().stream().map(m -> {
            GoalResponseDto.GoalMetricProgressDto mdto = new GoalResponseDto.GoalMetricProgressDto();
            mdto.setMetric(m.getMetric().name());
            mdto.setTargetValue(m.getTargetValue());
            mdto.setProgressValue(m.getProgressValue());
            double progressPercent = (m.getTargetValue() == 0)
                    ? 0
                    : (m.getProgressValue() / m.getTargetValue()) * 100.0;
            mdto.setProgressPercent(Math.min(progressPercent, 100.0));
            return mdto;
        }).collect(Collectors.toList());

        dto.setMetrics(metrics);
        return dto;
    }

    public Goal createGoal(Long userId, GoalRequestDto req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist: " + userId));
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setWorkoutType(req.getWorkoutType());
        goal.setPeriod(req.getPeriod());
        goal.setStartTime(alignNowToPeriodStart(req.getPeriod()));
        goal.setEndTime(nextPeriodEnd(goal.getStartTime(), req.getPeriod()));

        List<GoalMetricProgress> metrics = req.getMetrics().stream()
                .map(m -> {
                    GoalMetricProgress gmp = new GoalMetricProgress();
                    gmp.setMetric(m.getMetric());
                    gmp.setTargetValue(m.getTargetValue());
                    return gmp;
                })
                .collect(Collectors.toList());
        goal.setMetrics(metrics);

        return goalRepository.save(goal);
    }

    // When returning data, always call toDto()
    public List<GoalResponseDto> getGoalsByUser(Long userId, GoalStatus status) {
        List<Goal> goals = (status == null)
                ? goalRepository.findAllByUserId(userId)
                : goalRepository.findByUserIdAndStatus(userId, status);

        return goals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Called asynchronously when a new workout is uploaded.
     * Updates all active goals matching the workout type and within the goal’s
     * period.
     */
    @Async
    @Transactional
    public void updateGoalsForNewWorkout(Workout workout) {
        Long userId = workout.getUser().getId();
        WorkoutType workoutType = workout.getWorkoutType();
        LocalDateTime workoutTime = workout.getCreatedAt();

        // Fetch all active goals for this user and type
        List<Goal> activeGoals = goalRepository.findByUserIdAndWorkoutTypeAndStatus(
                userId, workoutType, GoalStatus.ACTIVE);

        for (Goal goal : activeGoals) {
            // Skip if workout is outside goal period
            if (workoutTime.isBefore(goal.getStartTime()) || workoutTime.isAfter(goal.getEndTime())) {
                continue;
            }

            // Increment progress for each metric in the goal
            for (GoalMetricProgress metric : goal.getMetrics()) {
                double increment = switch (metric.getMetric()) {
                    case DISTANCE -> workout.getDistanceKm();
                    case DURATION -> workout.getDurationMinutes();
                    case CALORIES -> workout.getCaloriesBurned();
                    case COUNT -> 1.0;
                };

                double newValue = metric.getProgressValue() + increment;
                metric.setProgressValue(newValue);

                double newPercent = Math.min(100.0, (newValue / metric.getTargetValue()) * 100.0);
                metric.setProgressPercent(newPercent);
            }

            // Mark goal as achieved if all metrics reach 100%
            boolean allAchieved = goal.getMetrics().stream()
                    .allMatch(m -> m.getProgressPercent() >= 100.0);
            if (allAchieved)
                goal.setStatus(GoalStatus.ACHIEVED);

            goalMetricRepository.saveAll(goal.getMetrics());
            goalRepository.save(goal);
        }

        System.out.printf("[GoalService] Updated %d active goals for userId=%d (%s)%n",
                activeGoals.size(), userId, workoutType);
    }

    // public Goal getGoalsDetailByUser(Long userId, Long goalId) {
    // return goalRepository.findOneByGoalId(userId, goalId);
    // }

    private LocalDateTime alignNowToPeriodStart(GoalPeriod period) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case DAILY -> today.atStartOfDay();
            case WEEKLY -> today.with(DayOfWeek.MONDAY).atStartOfDay();
            case MONTHLY -> today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            case YEARLY -> today.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
        };
    }

    private LocalDateTime nextPeriodEnd(LocalDateTime start, GoalPeriod period) {
        return switch (period) {
            case DAILY -> start.plusDays(1);
            case WEEKLY -> start.plusWeeks(1);
            case MONTHLY -> start.plusMonths(1);
            case YEARLY -> start.plusYears(1);
        };
    }
}
