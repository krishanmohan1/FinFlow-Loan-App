package com.capg.lpu.finflow.document.consumer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.capg.lpu.finflow.document.config.RabbitMQConfig;

/**
 * Message consumer for the Document service.
 * Listens for loan-related events from RabbitMQ and triggers internal document workflows.
 */
@Service
@RequiredArgsConstructor
public class LoanConsumer {

    private static final Logger log = LoggerFactory.getLogger(LoanConsumer.class);

    /**
     * Entry point for messages arriving on the configured loan queue.
     * Routes the message to specialized handlers based on the event type.
     *
     * @param message The raw string payload received from the message broker.
     */
    @RabbitListener(queues = RabbitMQConfig.LOAN_QUEUE)
    public void receiveMessage(String message) {

        log.info("[Document Service] Message received from queue: {}", message);

        if (message == null || message.isBlank()) {
            log.warn("Empty message received - skipping");
            return;
        }

        try {
            if (message.startsWith("NEW_LOAN_APPLICATION")) {
                handleNewLoanApplication(message);

            } else if (message.startsWith("LOAN_STATUS_UPDATED")) {
                handleLoanStatusUpdated(message);

            } else {
                log.warn("Unknown event type received: {}", message);
            }

        } catch (Exception e) {
            log.error("Error processing message: {} | Error: {}", message, e.getMessage(), e);
        }
    }

    /**
     * Handles events signaling the creation of a new loan application.
     * Extracts application metadata to prepare the document repository for the new loan.
     *
     * @param message The serialized message containing new application details.
     */
    private void handleNewLoanApplication(String message) {
        log.info("NEW LOAN APPLICATION event received");

        String loanId   = extractValue(message, "loanId");
        String username = extractValue(message, "username");
        String amount   = extractValue(message, "amount");
        String type     = extractValue(message, "type");

        log.info("Loan Details -> ID: {} | User: {} | Amount: {} | Type: {}",
                loanId, username, amount, type);

        log.info("New loan [{}] registered in Document Service - awaiting document uploads",
                loanId);
    }

    /**
     * Handles status update events for existing loan applications.
     * Synchronizes document status or visibility based on the new application state.
     *
     * @param message The serialized message containing status update details.
     */
    private void handleLoanStatusUpdated(String message) {
        log.info("LOAN STATUS UPDATED event received");

        String loanId    = extractValue(message, "loanId");
        String username  = extractValue(message, "username");
        String newStatus = extractValue(message, "newStatus");
        String remarks   = extractValue(message, "remarks");

        log.info("Status Update -> ID: {} | User: {} | Status: {} | Remarks: {}",
                loanId, username, newStatus, remarks);

        if ("APPROVED".equals(newStatus)) {
            log.info("Loan [{}] APPROVED - documents can now be marked as final", loanId);

        } else if ("REJECTED".equals(newStatus)) {
            log.warn("Loan [{}] REJECTED - Reason: {}", loanId, remarks);

        } else {
            log.info("Loan [{}] status changed to: {}", loanId, newStatus);
        }
    }

    /**
     * Utility method to extract specific key-value pairs from pipe-delimited message strings.
     *
     * @param message The full message string to parse.
     * @param key The specific key whose value should be retrieved.
     * @return The extracted value, or "UNKNOWN" if the key is not found.
     */
    private String extractValue(String message, String key) {
        try {
            String[] parts = message.split("\\|");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith(key + "=")) {
                    return part.substring((key + "=").length()).trim();
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract [{}] from message", key);
        }
        return "UNKNOWN";
    }
}