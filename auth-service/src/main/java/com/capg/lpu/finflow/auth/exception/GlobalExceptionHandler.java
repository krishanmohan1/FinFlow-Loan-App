package com.capg.lpu.finflow.auth.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Universal error interceptor for the identity domain providing structural mapping of application regressions to standardized API failure responses flawlessly correctly flawlessly smoothly natively.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Resolves operational business logic violations by mapping runtime regressions to 400 Bad Request status effortlessly.
     *
     * @param ex diagnostic exception carrying the operational failure metadata accurately flawlessly.
     * @return structural error payload suitable for downstream consumer consumption correctly flawlessly.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleRuntime(RuntimeException ex) {
        log.warn("Runtime error: {}", ex.getMessage());
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", ex.getMessage());
        return error;
    }

    /**
     * Fallback interceptor for unexpected critical regressions ensuring 500 Internal Server error status is returned flawlessly.
     *
     * @param ex intercepted unhandled application exception accurately flawslessly natively correctly.
     * @return generalized error payload ensuring sensitive application internals remain secure flawlessly.
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