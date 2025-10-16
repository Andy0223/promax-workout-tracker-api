package com.promax.workout.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Standard API Response Wrapper
 * Provides consistent response format for all API endpoints
 * 
 * @param <T> The type of data being returned
 * @author Promax Workout Tracker Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardResponseDto<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;
    private LocalDateTime timestamp;

    public StandardResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    // Success response factory methods
    public static <T> StandardResponseDto<T> success(T data) {
        StandardResponseDto<T> response = new StandardResponseDto<>();
        response.setSuccess(true);
        response.setMessage("Operation completed successfully");
        response.setData(data);
        return response;
    }

    public static <T> StandardResponseDto<T> success(String message, T data) {
        StandardResponseDto<T> response = new StandardResponseDto<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> StandardResponseDto<T> success(String message) {
        StandardResponseDto<T> response = new StandardResponseDto<>();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    // Error response factory methods
    public static <T> StandardResponseDto<T> error(String message) {
        StandardResponseDto<T> response = new StandardResponseDto<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public static <T> StandardResponseDto<T> error(String message, ErrorDetails errorDetails) {
        StandardResponseDto<T> response = new StandardResponseDto<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setError(errorDetails);
        return response;
    }

    // Nested class for error details
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private String field;
        private Object rejectedValue;
        private String path;

        public ErrorDetails() {
        }

        public ErrorDetails(String code, String field, Object rejectedValue) {
            this.code = code;
            this.field = field;
            this.rejectedValue = rejectedValue;
        }

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}