package com.capg.lpu.finflow.document.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event payload received from the application service for loan lifecycle changes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEventMessage {

    /**
     * Business event type.
     */
    private String eventType;

    /**
     * Loan identifier.
     */
    private Long loanId;

    /**
     * Applicant username.
     */
    private String username;

    /**
     * Loan amount when relevant.
     */
    private Double amount;

    /**
     * Loan type when relevant.
     */
    private String loanType;

    /**
     * Loan status when relevant.
     */
    private String status;

    /**
     * Optional remarks.
     */
    private String remarks;

    /**
     * Event creation timestamp.
     */
    private LocalDateTime occurredAt;
}
