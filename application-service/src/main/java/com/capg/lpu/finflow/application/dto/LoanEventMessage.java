package com.capg.lpu.finflow.application.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event payload emitted by the application service for loan lifecycle changes.
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
     * Requested loan amount when applicable.
     */
    private Double amount;

    /**
     * Requested loan type when applicable.
     */
    private String loanType;

    /**
     * Current loan status when applicable.
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
