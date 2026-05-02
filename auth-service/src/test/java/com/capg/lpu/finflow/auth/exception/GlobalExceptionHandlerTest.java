package com.capg.lpu.finflow.auth.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesValidationErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "username", "Username is required"));
        Method method = SampleController.class.getDeclaredMethod("sample", String.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        Map<String, Object> response = handler.handleValidation(exception);

        assertThat(response.get("status")).isEqualTo(400);
        assertThat(response.get("error")).isEqualTo("Validation failed");
        assertThat(((Map<?, ?>) response.get("details")).get("username")).isEqualTo("Username is required");
    }

    @Test
    void handlesConstraintRuntimeAndGeneralErrors() {
        ConstraintViolationException constraintViolationException =
                new ConstraintViolationException("invalid", Collections.<ConstraintViolation<?>>emptySet());

        assertThat(handler.handleConstraintViolation(constraintViolationException).get("status")).isEqualTo(400);
        assertThat(handler.handleRuntime(new RuntimeException("boom")).get("error")).isEqualTo("boom");
        assertThat(handler.handleGeneral(new Exception("oops")).get("status")).isEqualTo(500);
    }

    @SuppressWarnings("unused")
    private static class SampleController {
        public void sample(String value) {
        }
    }
}
