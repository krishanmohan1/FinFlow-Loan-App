package com.capg.lpu.finflow.admin.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for loan status transitions.
 * Used for simple status updates and administrative remarks.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanStatusUpdateRequest {

    /**
     * The target status for the loan application.
     */
    private String status;

    /**
     * Administrative remarks explaining the status change.
     */
    private String remarks;

    private Double interestRate;

    private Integer tenureMonths;

    private Double sanctionedAmount;

    private Double processingFee;

    private Double gstAmount;

    private Double monthlyEmi;

    private LocalDate firstEmiDate;

    private String borrowerDecision;
}
