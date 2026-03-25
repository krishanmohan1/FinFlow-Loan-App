package com.capg.lpu.finflow.document.consumer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.capg.lpu.finflow.document.config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
public class LoanConsumer {

    private static final Logger log = LoggerFactory.getLogger(LoanConsumer.class);

    // ✅ FIX: Document Service is the ONLY consumer of loanQueue
    // Application Service no longer competes on this queue
    @RabbitListener(queues = RabbitMQConfig.LOAN_QUEUE)
    public void receiveMessage(String message) {

        log.info("📩 [Document Service] Message received from queue: {}", message);

        if (message == null || message.isBlank()) {
            log.warn("⚠️ Empty message received — skipping");
            return;
        }

        try {
            // ✅ Route message to correct handler based on event type
            if (message.startsWith("NEW_LOAN_APPLICATION")) {
                handleNewLoanApplication(message);

            } else if (message.startsWith("LOAN_STATUS_UPDATED")) {
                handleLoanStatusUpdated(message);

            } else {
                log.warn("⚠️ Unknown event type received: {}", message);
            }

        } catch (Exception e) {
            // ✅ Never crash the consumer — log and move on
            log.error("🔴 Error processing message: {} | Error: {}", message, e.getMessage(), e);
        }
    }

    // ✅ Handle new loan application event
    // Triggered when a user successfully applies for a loan
    private void handleNewLoanApplication(String message) {
        log.info("🏦 NEW LOAN APPLICATION event received");

        // ✅ Parse key fields from message
        String loanId   = extractValue(message, "loanId");
        String username = extractValue(message, "username");
        String amount   = extractValue(message, "amount");
        String type     = extractValue(message, "type");

        log.info("📋 Loan Details →  ID: {} | User: {} | Amount: {} | Type: {}",
                loanId, username, amount, type);

        // ✅ Future automation ideas:
        // 1. Auto-create a document checklist for this loan
        //    e.g. HOME loan needs: AADHAAR + PAN + SALARY_SLIP + BANK_STATEMENT
        // 2. Send email/SMS notification to user to upload documents
        // 3. Create a pending document audit trail entry
        log.info("✅ New loan [{}] registered in Document Service — awaiting document uploads",
                loanId);
    }

    // ✅ Handle loan status update event
    // Triggered when ADMIN approves or rejects a loan
    private void handleLoanStatusUpdated(String message) {
        log.info("🔄 LOAN STATUS UPDATED event received");

        // ✅ Parse key fields from message
        String loanId    = extractValue(message, "loanId");
        String username  = extractValue(message, "username");
        String newStatus = extractValue(message, "newStatus");
        String remarks   = extractValue(message, "remarks");

        log.info("📋 Status Update → ID: {} | User: {} | Status: {} | Remarks: {}",
                loanId, username, newStatus, remarks);

        if ("APPROVED".equals(newStatus)) {
            log.info("✅ Loan [{}] APPROVED — documents can now be marked as final", loanId);
            // Future: Lock documents from further edits
            // Future: Trigger approval notification to user

        } else if ("REJECTED".equals(newStatus)) {
            log.warn("❌ Loan [{}] REJECTED — Reason: {}", loanId, remarks);
            // Future: Mark all documents for this loan as REJECTED
            // Future: Trigger rejection notification to user

        } else {
            log.info("ℹ️ Loan [{}] status changed to: {}", loanId, newStatus);
        }
    }

    // ✅ Helper: Extract value from message string
    // Message format: "EVENT_TYPE | key1=value1 | key2=value2"
    // Example: "NEW_LOAN_APPLICATION | loanId=5 | username=john"
    private String extractValue(String message, String key) {
        try {
            // Split by pipe and find the part containing the key
            String[] parts = message.split("\\|");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith(key + "=")) {
                    return part.substring((key + "=").length()).trim();
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Could not extract [{}] from message", key);
        }
        // Return "UNKNOWN" if key not found
        return "UNKNOWN";
    }
}