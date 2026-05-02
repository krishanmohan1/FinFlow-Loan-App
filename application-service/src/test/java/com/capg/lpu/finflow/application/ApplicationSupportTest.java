package com.capg.lpu.finflow.application;

import com.capg.lpu.finflow.application.config.OpenApiConfig;
import com.capg.lpu.finflow.application.config.RabbitMQConfig;
import com.capg.lpu.finflow.application.consumer.LoanConsumer;
import com.capg.lpu.finflow.application.dto.DocumentVerificationEvent;
import com.capg.lpu.finflow.application.dto.LoanEventMessage;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.exception.GlobalExceptionHandler;
import com.capg.lpu.finflow.application.exception.ResourceNotFoundException;
import com.capg.lpu.finflow.application.producer.LoanProducer;
import com.capg.lpu.finflow.application.repository.LoanRepository;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationSupportTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ConnectionFactory connectionFactory;

    @Test
    void rabbitAndOpenApiConfigBeansAreCreated() {
        RabbitMQConfig config = new RabbitMQConfig();
        Queue loanQueue = config.loanQueue();
        Queue appQueue = config.applicationEventQueue();
        Jackson2JsonMessageConverter converter = config.messageConverter();
        RabbitTemplate template = config.rabbitTemplate(connectionFactory);
        OpenAPI openAPI = new OpenApiConfig().applicationServiceOpenAPI();

        assertThat(loanQueue.getName()).isEqualTo(RabbitMQConfig.LOAN_QUEUE);
        assertThat(appQueue.getName()).isEqualTo(RabbitMQConfig.APPLICATION_EVENT_QUEUE);
        assertThat(converter).isNotNull();
        assertThat(template.getMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);
        assertThat(openAPI.getInfo().getTitle()).contains("Application Service");
    }

    @Test
    void loanProducerAndConsumerHandleMessages() {
        LoanProducer producer = new LoanProducer(rabbitTemplate);
        LoanEventMessage event = LoanEventMessage.builder().eventType("NEW_LOAN_APPLICATION").loanId(1L).build();
        producer.sendLoanCreated(event);
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.LOAN_QUEUE, event);

        LoanApplication loan = LoanApplication.builder().id(1L).username("user1").status("PENDING").remarks("doc pending").build();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanConsumer consumer = new LoanConsumer(loanRepository);
        DocumentVerificationEvent rejected = DocumentVerificationEvent.builder()
                .loanId("1")
                .documentType("PAN")
                .status("REJECTED")
                .remarks("blurred")
                .build();
        consumer.receiveMessage(rejected);

        assertThat(loan.getStatus()).isEqualTo("UNDER_REVIEW");
        assertThat(loan.getRemarks()).contains("Document PAN REJECTED");

        consumer.receiveMessage(DocumentVerificationEvent.builder().loanId("bad").status("VERIFIED").build());
        consumer.receiveMessage(null);
    }

    @Test
    void entityLifecycleAndExceptionsAreCovered() throws Exception {
        LoanApplication loan = LoanApplication.builder()
                .username("user1")
                .amount(50000.0)
                .loanType("HOME")
                .tenureMonths(120)
                .purpose("Buying a family home.")
                .build();
        loan.prePersist();
        assertThat(loan.getAppliedAt()).isNotNull();
        assertThat(loan.getStatus()).isEqualTo("PENDING");
        assertThat(loan.getBorrowerDecision()).isEqualTo("PENDING");

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "amount", "Amount required"));
        Method method = SampleController.class.getDeclaredMethod("sample", String.class);
        MethodArgumentNotValidException validationException =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        assertThat(handler.handleValidation(validationException).get("status")).isEqualTo(400);
        assertThat(handler.handleNotFound(new ResourceNotFoundException("missing")).get("status")).isEqualTo(404);
        assertThat(handler.handleBadRequest(new IllegalArgumentException("bad")).get("status")).isEqualTo(400);
        assertThat(handler.handleSecurity(new SecurityException("forbidden")).get("status")).isEqualTo(403);
        assertThat(handler.handleGeneral(new Exception("boom")).get("status")).isEqualTo(500);
        assertThat(handler.handleBadRequest(
                new ConstraintViolationException("invalid", Collections.<ConstraintViolation<?>>emptySet())).get("status")).isEqualTo(400);
    }

    @Test
    void mainBootstrapsApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ApplicationServiceApplication.main(new String[0]);
            springApplication.verify(() -> SpringApplication.run(ApplicationServiceApplication.class, new String[0]));
        }
    }

    @SuppressWarnings("unused")
    private static class SampleController {
        public void sample(String value) {
        }
    }
}
