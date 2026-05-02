package com.capg.lpu.finflow.application.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.capg.lpu.finflow.application.dto.DocumentVerificationEvent;
import com.capg.lpu.finflow.application.entity.LoanApplication;
import com.capg.lpu.finflow.application.repository.LoanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Message consumer service for the Application microservice.
 * Orchestrates responses to asynchronous events received via RabbitMQ.
 */


@Service
@Slf4j
@RequiredArgsConstructor
public class LoanConsumer {

    private final LoanRepository loanRepository;

    /**
     * Listener method for application-level events from the message broker.
     * Processes verified or rejected document notifications and other general events.
     *
     * @param event The typed payload received from the message queue.
     */
    @RabbitListener(queues = "applicationEventQueue")
    public void receiveMessage(DocumentVerificationEvent event) {
        log.info("[Application Service] Event received: {}", event);

        if (event == null || event.getLoanId() == null) {
            log.warn("Ignoring application event because loanId is missing");
            return;
        }

        try {
            Long loanId = Long.valueOf(event.getLoanId());
            LoanApplication loan = loanRepository.findById(loanId)
                    .orElse(null);

            if (loan == null) {
                log.warn("Loan {} not found for document event", loanId);
                return;
            }

            String existingRemarks = loan.getRemarks() == null ? "" : loan.getRemarks().trim();
            String auditEntry = String.format(
                    "Document %s %s%s",
                    event.getDocumentType(),
                    event.getStatus(),
                    event.getRemarks() == null || event.getRemarks().isBlank() ? "" : " - " + event.getRemarks().trim()
            );

            if ("REJECTED".equals(event.getStatus()) && !isFinalizedStatus(loan.getStatus())) {
                loan.setStatus("UNDER_REVIEW");
            }

            loan.setRemarks(existingRemarks.isBlank() ? auditEntry : existingRemarks + " | " + auditEntry);
            loanRepository.save(loan);
            log.info("Loan {} updated asynchronously after document {}", loanId, event.getStatus());
        } catch (NumberFormatException ex) {
            log.warn("Invalid loanId [{}] in document event", event.getLoanId());
        }
    }

    private boolean isFinalizedStatus(String status) {
        return "ACTIVE".equals(status)
                || "REJECTED".equals(status)
                || "WITHDRAWN".equals(status)
                || "OFFER_DECLINED".equals(status);
    }
}
