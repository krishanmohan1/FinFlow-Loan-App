package com.capg.lpu.finflow.document;

import com.capg.lpu.finflow.document.config.OpenApiConfig;
import com.capg.lpu.finflow.document.config.RabbitMQConfig;
import com.capg.lpu.finflow.document.consumer.LoanConsumer;
import com.capg.lpu.finflow.document.dto.DocumentVerificationEvent;
import com.capg.lpu.finflow.document.dto.LoanEventMessage;
import com.capg.lpu.finflow.document.entity.Document;
import com.capg.lpu.finflow.document.exception.GlobalExceptionHandler;
import com.capg.lpu.finflow.document.exception.ResourceNotFoundException;
import com.capg.lpu.finflow.document.producer.DocumentEventProducer;
import com.capg.lpu.finflow.document.repository.DocumentRepository;
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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentSupportTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private ConnectionFactory connectionFactory;

    @Test
    void configProducerAndConsumerAreCovered() {
        RabbitMQConfig config = new RabbitMQConfig();
        Queue loanQueue = config.loanQueue();
        Queue appQueue = config.applicationEventQueue();
        Jackson2JsonMessageConverter converter = config.messageConverter();
        RabbitTemplate template = config.rabbitTemplate(connectionFactory);
        OpenAPI openAPI = new OpenApiConfig().documentServiceOpenAPI();

        assertThat(loanQueue.getName()).isEqualTo(RabbitMQConfig.LOAN_QUEUE);
        assertThat(appQueue.getName()).isEqualTo(RabbitMQConfig.APPLICATION_EVENT_QUEUE);
        assertThat(converter).isNotNull();
        assertThat(template.getMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);
        assertThat(openAPI.getInfo().getTitle()).contains("Document Service");

        DocumentEventProducer producer = new DocumentEventProducer(rabbitTemplate);
        DocumentVerificationEvent verificationEvent = DocumentVerificationEvent.builder().loanId("1").status("VERIFIED").build();
        producer.publishVerificationEvent(verificationEvent);
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.APPLICATION_EVENT_QUEUE, verificationEvent);

        Document document = Document.builder()
                .id(1L)
                .loanId("1")
                .username("user1")
                .documentType("AADHAAR")
                .fileName("aadhaar.pdf")
                .filePath("/tmp/aadhaar.pdf")
                .verificationStatus("VERIFIED")
                .verifiedRemarks("manual check")
                .build();
        when(documentRepository.findByLoanId("1")).thenReturn(List.of(document));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanConsumer consumer = new LoanConsumer(documentRepository);
        consumer.receiveMessage(LoanEventMessage.builder().eventType("NEW_LOAN_APPLICATION").loanId(1L).username("user1").loanType("HOME").amount(50000.0).build());
        consumer.receiveMessage(LoanEventMessage.builder().eventType("LOAN_STATUS_UPDATED").loanId(1L).username("user1").status("ACTIVE").remarks("accepted").build());

        assertThat(document.getVerifiedRemarks()).contains("Loan approved - document locked as final");
        consumer.receiveMessage(LoanEventMessage.builder().eventType("LOAN_STATUS_UPDATED").loanId(1L).status("OFFER_MADE").build());
        consumer.receiveMessage(LoanEventMessage.builder().eventType("LOAN_STATUS_UPDATED").loanId(1L).status("REJECTED").remarks("bad docs").build());
        consumer.receiveMessage(LoanEventMessage.builder().eventType("UNKNOWN").build());
        consumer.receiveMessage(null);
    }

    @Test
    void documentEntityAndExceptionHandlerAreCovered() throws Exception {
        Document document = Document.builder()
                .username("user1")
                .loanId("1")
                .documentType("AADHAAR")
                .fileName("aadhaar.pdf")
                .filePath("/tmp/aadhaar.pdf")
                .build();
        document.prePersist();
        LocalDateTime firstUpdatedAt = document.getUpdatedAt();
        assertThat(document.getCreatedAt()).isNotNull();
        assertThat(document.getVerificationStatus()).isEqualTo("PENDING");
        document.preUpdate();
        assertThat(document.getUpdatedAt()).isAfterOrEqualTo(firstUpdatedAt);

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "status", "Status required"));
        Method method = SampleController.class.getDeclaredMethod("sample", String.class);
        MethodArgumentNotValidException validationException =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        assertThat(handler.handleValidation(validationException).get("status")).isEqualTo(400);
        assertThat(handler.handleNotFound(new ResourceNotFoundException("missing")).get("status")).isEqualTo(404);
        assertThat(handler.handleSecurity(new SecurityException("forbidden")).get("status")).isEqualTo(403);
        assertThat(handler.handleRuntime(new RuntimeException("bad")).get("status")).isEqualTo(400);
        assertThat(handler.handleBadRequest(
                new ConstraintViolationException("invalid", Collections.<ConstraintViolation<?>>emptySet())).get("status")).isEqualTo(400);
        assertThat(handler.handleGeneral(new Exception("oops")).get("status")).isEqualTo(500);
    }

    @Test
    void mainBootstrapsApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            DocumentServiceApplication.main(new String[0]);
            springApplication.verify(() -> SpringApplication.run(DocumentServiceApplication.class, new String[0]));
        }
    }

    @SuppressWarnings("unused")
    private static class SampleController {
        public void sample(String value) {
        }
    }
}
