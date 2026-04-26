package com.capg.lpu.finflow.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralized exception handling for the Application microservice.
 * Intercepts specific exceptions and provides standardized error responses across all REST endpoints.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", "Validation failed");
        error.put("details", ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage(),
                        (first, second) -> first,
                        LinkedHashMap::new
                )));
        return error;
    }

    /**
     * Handles ResourceNotFoundException and returns a 404 Not Found response.
     *
     * @param ex The exception indicating a missing resource.
     * @return A map containing error details including timestamp, status, and message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 404);
        error.put("error", ex.getMessage());
        return error;
    }

    /**
     * Handles generic exceptions and returns a 500 Internal Server Error response.
     * Prevents internal implementation details from being exposed to the client.
     *
     * @param ex The unexpected exception encountered during execution.
     * @return A map containing general error information and a generic message.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 500);
        error.put("error", "Internal server error");
        return error;
    }

    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(Exception ex) {
        log.warn("Bad request: {}", ex.getMessage());
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", ex.getMessage());
        return error;
    }

    /**
     * Handles SecurityException and returns a 403 Forbidden response.
     * Used for unauthorized access attempts to administrative or restricted endpoints.
     *
     * @param ex The security exception indicating an authorization failure.
     * @return A map containing security violation details.
     */
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleSecurity(SecurityException ex) {
        log.warn("Security violation: {}", ex.getMessage());
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 403);
        error.put("error", ex.getMessage());
        return error;
    }
}
