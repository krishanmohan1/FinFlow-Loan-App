package com.capg.lpu.finflow.document.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.capg.lpu.finflow.document.config.RabbitMQConfig;
import com.capg.lpu.finflow.document.dto.LoanEventMessage;
import com.capg.lpu.finflow.document.entity.Document;
import com.capg.lpu.finflow.document.repository.DocumentRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Message consumer for the Document service.
 * Listens for loan-related events from RabbitMQ and triggers internal document workflows.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LoanConsumer {

    private final DocumentRepository documentRepository;

    /**
     * Entry point for messages arriving on the configured loan queue.
     * Routes the message to specialized handlers based on the event type.
     *
     * @param message The event payload received from the message broker.
     */
    @RabbitListener(queues = RabbitMQConfig.LOAN_QUEUE)
    public void receiveMessage(LoanEventMessage message) {

        log.info("[Document Service] Message received from queue: {}", message);

        if (message == null || message.getEventType() == null || message.getEventType().isBlank()) {
            log.warn("Empty message received - skipping");
            return;
        }

        try {
            if ("NEW_LOAN_APPLICATION".equals(message.getEventType())) {
                handleNewLoanApplication(message);

            } else if ("LOAN_STATUS_UPDATED".equals(message.getEventType())) {
                handleLoanStatusUpdated(message);

            } else {
                log.warn("Unknown event type received: {}", message.getEventType());
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
    private void handleNewLoanApplication(LoanEventMessage message) {
        log.info("NEW LOAN APPLICATION event received");

        log.info("Loan Details -> ID: {} | User: {} | Amount: {} | Type: {}",
                message.getLoanId(), message.getUsername(), message.getAmount(), message.getLoanType());

        log.info("New loan [{}] registered in Document Service - awaiting document uploads",
                message.getLoanId());
    }

    /**
     * Handles status update events for existing loan applications.
     * Synchronizes document status or visibility based on the new application state.
     *
     * @param message The serialized message containing status update details.
     */
    private void handleLoanStatusUpdated(LoanEventMessage message) {
        log.info("LOAN STATUS UPDATED event received");

        log.info("Status Update -> ID: {} | User: {} | Status: {} | Remarks: {}",
                message.getLoanId(), message.getUsername(), message.getStatus(), message.getRemarks());

        if ("APPROVED".equals(message.getStatus())) {
            log.info("Loan [{}] APPROVED - documents can now be marked as final", message.getLoanId());

            documentRepository.findByLoanId(String.valueOf(message.getLoanId()))
                    .forEach(document -> markDocumentComplete(document));

        } else if ("REJECTED".equals(message.getStatus())) {
            log.warn("Loan [{}] REJECTED - Reason: {}", message.getLoanId(), message.getRemarks());

        } else {
            log.info("Loan [{}] status changed to: {}", message.getLoanId(), message.getStatus());
        }
    }

    /**
     * Marks documents as complete once the full loan is approved.
     *
     * @param document document to update
     */
    private void markDocumentComplete(Document document) {
        String existingRemarks = document.getVerifiedRemarks() == null ? "" : document.getVerifiedRemarks().trim();
        String approvedRemark = "Loan approved - document locked as final";
        document.setVerifiedRemarks(existingRemarks.isBlank()
                ? approvedRemark
                : existingRemarks + " | " + approvedRemark);
        documentRepository.save(document);
    }
}
