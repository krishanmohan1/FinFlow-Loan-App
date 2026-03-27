package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

/**
 * Data Transfer Object for administrative loan decisions.
 * Carries the final decision, remarks, and financial parameters for approved loans.
 */
@Data
public class DecisionRequest {

    /**
     * The decision for the loan application (e.g., APPROVED, REJECTED).
     */
    private String decision;

    /**
     * Administrative remarks or justification for the decision.
     */
    private String remarks;

    /**
     * The interest rate assigned to the approved loan.
     */
    private Double interestRate;

    /**
     * The duration of the loan in months.
     */
    private Integer tenureMonths;

    /**
     * The final amount sanctioned for the loan.
     */
    private Double sanctionedAmount;
}