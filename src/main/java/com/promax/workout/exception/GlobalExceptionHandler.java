package com.promax.workout.exception;

import com.promax.workout.dto.StandardResponseDto;
import com.promax.workout.dto.StandardResponseDto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global Exception Handler
 * Provides unified error responses for all API exceptions
 * 
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors (e.g. @Valid failed)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponseDto<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorDetails> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapToErrorDetails)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponseDto.error("Validation failed",
                        new ErrorDetails("VALIDATION_ERROR", null, errors)));
    }

    private ErrorDetails mapToErrorDetails(FieldError fieldError) {
        ErrorDetails details = new ErrorDetails();
        details.setCode("INVALID_FIELD");
        details.setField(fieldError.getField());
        details.setRejectedValue(fieldError.getRejectedValue());
        details.setPath(fieldError.getObjectName());
        return details;
    }

    /**
     * Handle invalid parameter types in request
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardResponseDto<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorDetails details = new ErrorDetails();
        details.setCode("TYPE_MISMATCH");
        details.setField(ex.getName());
        details.setRejectedValue(ex.getValue());
        details.setPath(ex.getParameter().getParameterType().getSimpleName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponseDto.error("Invalid parameter type", details));
    }

    /**
     * Handle authentication failures (e.g., incorrect username or password)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponseDto<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorDetails details = new ErrorDetails();
        details.setCode("AUTH_ERROR");
        details.setPath("/v1/users/login");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(StandardResponseDto.error(ex.getMessage(), details));
    }

    /**
     * Handle generic exceptions (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponseDto<Void>> handleGeneralException(Exception ex) {
        ErrorDetails details = new ErrorDetails();
        details.setCode("INTERNAL_ERROR");
        details.setPath("Unknown");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardResponseDto.error("An unexpected error occurred", details));
    }
}
