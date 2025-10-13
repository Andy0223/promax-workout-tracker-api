package com.promax.workout.service;

import com.promax.workout.dto.UserRegistrationDto;
import com.promax.workout.entity.User;
import com.promax.workout.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService Unit Tests
 * Tests core business logic of the user service layer
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto registrationDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Prepare test data
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setFullName("Test User");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void testRegisterUser_Success() {
        // Mock user does not exist
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Execute test
        User result = userService.registerUser(registrationDto);

        // Verify result
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getFullName());
        assertEquals("test@example.com", result.getEmail());

        // Verify method calls
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameExists() {
        // Mock username already exists
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Execute test and verify exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(registrationDto));

        assertEquals("Username already exists: testuser", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailExists() {
        // Mock email already exists
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Execute test and verify exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(registrationDto));

        assertEquals("Email already exists: test@example.com", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginUser_Success() {
        // Mock login success
        when(userRepository.findByUsernameAndPassword("testuser", "password123"))
                .thenReturn(Optional.of(testUser));

        // Execute test
        User result = userService.loginUser("testuser", "password123");

        // Verify result
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        // Verify method calls
        verify(userRepository).findByUsernameAndPassword("testuser", "password123");
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        // Mock login failed
        when(userRepository.findByUsernameAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Execute test and verify exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.loginUser("testuser", "wrongpassword"));

        assertEquals("Incorrect username or password", exception.getMessage());
        verify(userRepository).findByUsernameAndPassword("testuser", "wrongpassword");
    }

    @Test
    void testFindUserById_Success() {
        // Mock user exists
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Execute test
        Optional<User> result = userService.findUserById(1L);

        // Verify result
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());

        // Verify method calls
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindUserById_NotFound() {
        // Mock user does not exist
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute test
        Optional<User> result = userService.findUserById(999L);

        // Verify result
        assertFalse(result.isPresent());

        // Verify method calls
        verify(userRepository).findById(999L);
    }

    @Test
    void testIsUsernameExists() {
        // Mock username exists
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Execute test
        boolean result = userService.isUsernameExists("testuser");

        // Verify result
        assertTrue(result);

        // Verify method calls
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void testIsEmailExists() {
        // Mock email exists
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Execute test
        boolean result = userService.isEmailExists("test@example.com");

        // Verify result
        assertTrue(result);

        // Verify method calls
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void testGetUserCount() {
        // Mock user total
        when(userRepository.countAllUsers()).thenReturn(5L);

        // Execute test
        long result = userService.getUserCount();

        // Verify result
        assertEquals(5L, result);

        // Verify method calls
        verify(userRepository).countAllUsers();
    }
}
