package com.promax.workout.service;

import com.promax.workout.dto.WorkoutUploadDto;
import com.promax.workout.entity.User;
import com.promax.workout.entity.Workout;
import com.promax.workout.repository.UserRepository;
import com.promax.workout.repository.WorkoutRepository;
import com.promax.workout.enums.WorkoutType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * WorkoutService Unit Tests
 * Tests the core business logic of the workout record service layer
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkoutService workoutService;

    private User testUser;
    private WorkoutUploadDto uploadDto;
    private Workout testWorkout;

    @BeforeEach
    void setUp() {
        // Prepare test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        // Prepare upload DTO
        uploadDto = new WorkoutUploadDto();
        uploadDto.setWorkoutType(WorkoutType.RUNNING);
        uploadDto.setDurationMinutes(30);
        uploadDto.setDistanceKm(5.0);
        uploadDto.setCaloriesBurned(300);
        uploadDto.setNotes("test exercise record");

        // Prepare test workout record
        testWorkout = new Workout();
        testWorkout.setId(1L);
        testWorkout.setWorkoutType(WorkoutType.RUNNING);
        testWorkout.setDurationMinutes(30);
        testWorkout.setDistanceKm(5.0);
        testWorkout.setCaloriesBurned(300);
        testWorkout.setNotes("test exercise record");
        testWorkout.setUser(testUser);
    }

    @Test
    void testUploadWorkout_Success() {
        // Mock user exists
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.save(any(Workout.class))).thenReturn(testWorkout);

        // Execute test
        Workout result = workoutService.uploadWorkout(1L, uploadDto);

        // Verify result
        assertNotNull(result);
        assertEquals(WorkoutType.RUNNING, result.getWorkoutType());
        assertEquals(30, result.getDurationMinutes());
        assertEquals(new BigDecimal("5.0"), result.getDistanceKm());
        assertEquals(300, result.getCaloriesBurned());
        assertEquals("test exercise record", result.getNotes());

        // Verify method calls
        verify(userRepository).findById(1L);
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void testUploadWorkout_UserNotFound() {
        // Mock user does not exist
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute test and verify exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workoutService.uploadWorkout(999L, uploadDto));

        assertEquals("User does not exist: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    void testGetUserWorkouts_Success() {
        // Prepare test data
        List<Workout> workouts = Arrays.asList(testWorkout);
        when(workoutRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(workouts);

        // Execute test
        List<Workout> result = workoutService.getUserWorkouts(1L);

        // Verify result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(WorkoutType.RUNNING, result.get(0).getWorkoutType());

        // Verify method calls
        verify(workoutRepository).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testGetRecentWorkouts_Success() {
        // Prepare test data
        List<Workout> workouts = Arrays.asList(testWorkout);
        Pageable pageable = PageRequest.of(0, 5);
        when(workoutRepository.findRecentWorkoutsByUserId(1L, pageable)).thenReturn(workouts);

        // Execute test
        List<Workout> result = workoutService.getRecentWorkouts(1L, 5);

        // Verify result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(WorkoutType.RUNNING, result.get(0).getWorkoutType());

        // Verify method calls
        verify(workoutRepository).findRecentWorkoutsByUserId(1L, pageable);
    }

    @Test
    void testGetWorkoutById_Success() {
        // Mock workout record exists
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));

        // Execute test
        Optional<Workout> result = workoutService.getWorkoutById(1L);

        // Verify result
        assertTrue(result.isPresent());
        assertEquals(WorkoutType.RUNNING, result.get().getWorkoutType());

        // Verify method calls
        verify(workoutRepository).findById(1L);
    }

    @Test
    void testGetWorkoutById_NotFound() {
        // Mock workout record does not exist
        when(workoutRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute test
        Optional<Workout> result = workoutService.getWorkoutById(999L);

        // Verify result
        assertFalse(result.isPresent());

        // Verify method calls
        verify(workoutRepository).findById(999L);
    }

    @Test
    void testUpdateWorkout_Success() {
        // Mock workout record exists
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(workoutRepository.save(any(Workout.class))).thenReturn(testWorkout);

        // Execute test
        Workout result = workoutService.updateWorkout(1L, uploadDto);

        // Verify result
        assertNotNull(result);
        assertEquals(WorkoutType.RUNNING, result.getWorkoutType());

        // Verify method calls
        verify(workoutRepository).findById(1L);
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void testUpdateWorkout_NotFound() {
        // Mock workout record does not exist
        when(workoutRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute test and verify exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workoutService.updateWorkout(999L, uploadDto));

        assertEquals("Workout record not found: 999", exception.getMessage());
        verify(workoutRepository).findById(999L);
        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    void testDeleteWorkout_Success() {
        // Mock workout record exists
        when(workoutRepository.existsById(1L)).thenReturn(true);

        // Execute test
        assertDoesNotThrow(() -> workoutService.deleteWorkout(1L));

        // Verify method calls
        verify(workoutRepository).existsById(1L);
        verify(workoutRepository).deleteById(1L);
    }

    @Test
    void testDeleteWorkout_NotFound() {
        // Mock workout record does not exist
        when(workoutRepository.existsById(999L)).thenReturn(false);

        // Execute test and verify exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workoutService.deleteWorkout(999L));

        assertEquals("workout record doesn't exist: 999", exception.getMessage());
        verify(workoutRepository).existsById(999L);
        verify(workoutRepository, never()).deleteById(anyLong());
    }
}
