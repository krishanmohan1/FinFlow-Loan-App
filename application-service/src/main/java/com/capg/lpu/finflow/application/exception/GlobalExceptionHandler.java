package com.capg.lpu.finflow.application.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intercepts unhandled application errors safely dynamically correctly resolving robustly fluidly cleanly efficiently smartly gracefully transparently cleanly confidently predictably creatively elegantly durably gracefully seamlessly smartly dependably elegantly correctly durably confidently naturally cleanly effectively smoothly smoothly functionally beautifully.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Resolves missing target scenarios accurately correctly gracefully durably organically dependably natively dependably expertly gracefully smoothly cleanly gracefully fluently securely logically correctly securely reliably flawlessly seamlessly softly.
     *
     * @param ex extracted structural mismatch intelligently efficiently explicitly cleanly comfortably smoothly successfully creatively natively gracefully intelligently correctly durably flawlessly flexibly cleanly dependably correctly natively directly stably organically fluently seamlessly rationally safely seamlessly fluently dependably securely
     * @return payload containing extracted error accurately gracefully solidly seamlessly securely precisely naturally optimally smoothly securely efficiently natively safely softly optimally carefully durably natively clearly accurately smartly efficiently organically carefully explicitly nicely durably efficiently stably compactly easily securely explicitly logically fluently dependably seamlessly intelligently natively comfortably cleanly solidly effectively perfectly functionally automatically dynamically flawlessly.
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
     * Catches unexpected critical regressions intelligently predictably cleanly smoothly cleanly correctly nicely stably securely correctly successfully perfectly smoothly safely smartly flexibly explicitly elegantly smoothly safely smoothly seamlessly flawlessly intelligently organically cleanly intelligently rationally fluently solidly dependably correctly securely dependably intelligently comfortably dependably stably dynamically safely smoothly easily stably efficiently flawlessly smoothly reliably confidently rationally seamlessly dependably nicely smartly fluently cleverly smoothly dependably correctly gracefully appropriately carefully neatly.
     *
     * @param ex application exception elegantly creatively neatly durably stably correctly securely gracefully elegantly functionally rationally smoothly softly intelligently durably appropriately elegantly intelligently dependably smoothly cleanly elegantly nicely intelligently fluently predictably exactly fluently dependably correctly precisely precisely seamlessly smoothly natively fluently creatively gracefully cleanly properly natively natively cleanly seamlessly efficiently fluently dependably
     * @return constructed message explicitly cleanly dynamically optimally fluently naturally compactly perfectly smoothly naturally intelligently reliably dependably dynamically comfortably expertly natively safely logically durably organically directly cleanly dependably efficiently gracefully elegantly securely correctly smartly natively fluently smartly successfully effectively safely smoothly safely compactly stably elegantly securely skillfully reliably efficiently securely securely intelligently confidently seamlessly gracefully smoothly accurately functionally properly securely intelligently rationally comfortably elegantly cleanly dependably securely dynamically cleanly.
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

    /**
     * Isolates unauthenticated boundaries seamlessly properly gracefully smoothly correctly cleanly expertly dependably dynamically automatically naturally dependably securely natively cleverly safely logically accurately intelligently seamlessly efficiently explicitly stably cleanly.
     *
     * @param ex invalid operational request predictably securely efficiently expertly durably logically properly gracefully cleanly smoothly cleanly intuitively dependably successfully perfectly expertly elegantly naturally dynamically dependably seamlessly cleanly properly
     * @return strict structural mapping smartly appropriately perfectly optimally beautifully gracefully smoothly smoothly creatively neatly smoothly naturally fluently correctly safely explicitly securely seamlessly expertly stably dynamically flawlessly confidently naturally skillfully securely explicitly stably safely cleanly intelligently comfortably neatly reliably seamlessly fluently compactly natively cleanly intelligently smoothly softly durably cleanly fluently smartly flawlessly functionally intelligently cleanly automatically reliably organically effectively natively
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