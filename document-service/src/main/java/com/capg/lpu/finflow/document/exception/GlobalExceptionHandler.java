package com.capg.lpu.finflow.document.exception;

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
 * Global exception handler for the Document microservice.
 * Intercepts common exceptions and maps them to standardized RESTful error responses.
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
     * @param ex The exception indicating that a requested document or resource was not found.
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
     * Handles SecurityException and returns a 403 Forbidden response.
     *
     * @param ex The exception indicating an unauthorized access attempt to a document.
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

    /**
     * Handles generic RuntimeException and returns a 400 Bad Request response.
     * Used for general business logic or validation failures.
     *
     * @param ex The runtime exception encountered during execution.
     * @return A map containing the error message and bad request status.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleRuntime(RuntimeException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", ex.getMessage());
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
     * Handles all other unhandled exceptions and returns a 500 Internal Server Error response.
     *
     * @param ex The unexpected exception.
     * @return A map containing a generic internal server error message.
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
}
