package com.capg.lpu.finflow.document.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event payload published back to the application service after a document
 * verification decision is made.
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
     * Applicant username.
     */
    private String username;

    /**
     * Document type that was reviewed.
     */
    private String documentType;

    /**
     * Verification result.
     */
    private String status;

    /**
     * Optional administrative remarks.
     */
    private String remarks;

    /**
     * Event creation timestamp.
     */
    private LocalDateTime occurredAt;
}
