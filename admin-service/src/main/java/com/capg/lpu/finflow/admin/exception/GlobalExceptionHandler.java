package com.capg.lpu.finflow.admin.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralized exception orchestration layer providing unified error response mapping for the administrative microservice flawlessly correctly flawlessly smoothly natively.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Intercepts and processes illegal argument violations by generating a standardized error response map flawlessly correctly.
     *
     * @param ex the intercepted illegal argument exception instance accurately flawlessly.
     * @return a structured map containing temporal stamps, status codes, and descriptive error metadata correctly natively.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", ex.getMessage());
        return error;
    }

    /**
     * Captures and handles security-related boundary violations by enforcing forbidden access responses flawlessly correctly natively.
     *
     * @param ex the intercepted security exception instance accurately flawlessly flawlessly correctly.
     * @return a structured map reflecting the security violation with appropriate HTTP status codes correctly natively flawlessly.
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
     * Serves as a catch-all processor for unhandled runtime exceptions ensuring graceful failure responses without exposing internal traces flawlessy correctly flawlessly.
     *
     * @param ex the base exception model captured by the interceptor accurately flawlessly flawlessly.
     * @return a standardized internal server error response mapping providing required diagnostic metadata safely correctly natively flawlessly flawlessly.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneral(Exception ex) {
        log.error("Admin service error: {}", ex.getMessage(), ex);
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 500);
        error.put("error", "Admin service error: " + ex.getMessage());
        return error;
    }
}