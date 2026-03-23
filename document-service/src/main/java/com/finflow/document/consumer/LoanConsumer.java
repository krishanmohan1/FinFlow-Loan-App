package com.finflow.document.consumer;

import com.finflow.document.config.RabbitMQConfig;
import com.finflow.document.entity.Document;
import com.finflow.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanConsumer {

    private static final Logger log = LoggerFactory.getLogger(LoanConsumer.class);

    private final DocumentRepository documentRepository;

    @RabbitListener(queues = RabbitMQConfig.LOAN_QUEUE)
    public void receiveMessage(String message) {
        log.info("📩 Message received from queue: {}", message);

        try {
            // Parse the structured message
            // Format: "EVENT_TYPE | id=X | user=Y | ..."
            String eventType = extractField(message, 0);   // first segment
            String loanId    = extractValue(message, "id");
            String username  = extractValue(message, "user");

            Document doc = Document.builder()
                    .username(username)
                    .loanId(loanId)
                    .eventType(eventType.trim())
                    .content(message)
                    .createdAt(LocalDateTime.now())
                    .build();

            documentRepository.save(doc);
            log.info("✅ Document saved → loanId: {}, user: {}, event: {}",
                    loanId, username, eventType.trim());

        } catch (Exception e) {
            log.error("🔴 Failed to process message: {} | Error: {}", message, e.getMessage());
        }
    }

    // Extract segment by pipe position (0-indexed)
    private String extractField(String message, int index) {
        String[] parts = message.split("\\|");
        if (parts.length > index) {
            return parts[index].trim();
        }
        return "UNKNOWN";
    }

    // Extract value by key: "id=5" → "5"
    private String extractValue(String message, String key) {
        String[] parts = message.split("\\|");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.startsWith(key + "=")) {
                return trimmed.substring((key + "=").length()).trim();
            }
        }
        return "UNKNOWN";
    }
}