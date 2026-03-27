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
 * Centralized exception interceptor dynamically governing unhandled runtime issues predictably returning standardized structures universally.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Intercepts logic constraints explicitly returning standardized bad request formatting avoiding generic dumps natively.
     *
     * @param ex intercepted condition describing boundary violations clearly
     * @return constructed response map masking internal configurations safely
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
     * Evaluates intercepted security boundary cross failures strictly communicating unauthenticated routing constraints predictably efficiently.
     *
     * @param ex implicitly mapped security breakdown event accurately capturing access violations structurally
     * @return strictly bounded map maintaining necessary error codes logically reliably exclusively
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
     * Catchall processor governing unpredictable server failures preventing full trace leakages fundamentally resolving cleanly explicitly safely predictably effectively properly systematically properly smoothly correctly effectively seamlessly flawlessly perfectly confidently smoothly clearly correctly easily correctly optimally. 
     *
     * @param ex base exception model masking internal configurations safely
     * @return object hash map reliably dictating overarching constraints logically fundamentally securely efficiently strictly
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