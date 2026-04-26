package com.capg.lpu.finflow.application.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event payload emitted by the document service whenever a document verification
 * decision should asynchronously affect a loan application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVerificationEvent {

    /**
     * Business event name.
     */
    private String eventType;

    /**
     * Loan identifier associated with the document.
     */
    private String loanId;

    /**
     * Username of the loan applicant.
     */
    private String username;

    /**
     * Document type that was verified or rejected.
     */
    private String documentType;

    /**
     * Result of the verification decision.
     */
    private String status;

    /**
     * Optional administrative remarks.
     */
    private String remarks;

    /**
     * When the event was created.
     */
    private LocalDateTime occurredAt;
}
