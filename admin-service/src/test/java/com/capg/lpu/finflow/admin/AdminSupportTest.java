package com.capg.lpu.finflow.admin;

import com.capg.lpu.finflow.admin.config.FeignConfig;
import com.capg.lpu.finflow.admin.config.OpenApiConfig;
import com.capg.lpu.finflow.admin.entity.AdminActionAudit;
import com.capg.lpu.finflow.admin.entity.ReportSnapshot;
import com.capg.lpu.finflow.admin.exception.GlobalExceptionHandler;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.swagger.v3.oas.models.OpenAPI;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class AdminSupportTest {

    @Test
    void feignAndOpenApiConfigAreCovered() {
        RequestInterceptor interceptor = new FeignConfig().adminHeaderInterceptor();
        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);
        OpenAPI openAPI = new OpenApiConfig().adminServiceOpenAPI();

        assertThat(template.headers()).containsKeys("X-Auth-Role", "X-Auth-Username");
        assertThat(openAPI.getInfo().getTitle()).contains("Admin Service");
    }

    @Test
    void entitiesAndExceptionHandlerAreCovered() throws Exception {
        AdminActionAudit audit = AdminActionAudit.builder().actionType("CREATE").targetType("LOAN").outcome("SUCCESS").build();
        audit.prePersist();
        ReportSnapshot snapshot = ReportSnapshot.builder().snapshotType("SUMMARY").payloadJson("{}").build();
        snapshot.prePersist();

        assertThat(audit.getCreatedAt()).isNotNull();
        assertThat(snapshot.getGeneratedAt()).isNotNull();

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "decision", "Decision required"));
        Method method = SampleController.class.getDeclaredMethod("sample", String.class);
        MethodArgumentNotValidException validationException =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        Map<String, Object> validation = handler.handleValidation(validationException);
        assertThat(validation.get("status")).isEqualTo(400);
        assertThat(handler.handleBadRequest(new IllegalArgumentException("bad")).get("status")).isEqualTo(400);
        assertThat(handler.handleConstraintViolation(
                new ConstraintViolationException("invalid", Collections.<ConstraintViolation<?>>emptySet())).get("status")).isEqualTo(400);
        assertThat(handler.handleSecurity(new SecurityException("forbidden")).get("status")).isEqualTo(403);
        assertThat(handler.handleGeneral(new Exception("oops")).get("status")).isEqualTo(500);
    }

    @Test
    void mainBootstrapsApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            AdminServiceApplication.main(new String[0]);
            springApplication.verify(() -> SpringApplication.run(AdminServiceApplication.class, new String[0]));
        }
    }

    @SuppressWarnings("unused")
    private static class SampleController {
        public void sample(String value) {
        }
    }
}
