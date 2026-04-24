package com.capg.lpu.finflow.admin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Decision is required")
    @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "Decision must be APPROVED or REJECTED")
    private String decision;

    /**
     * Administrative remarks or justification for the decision.
     */
    @Size(max = 500, message = "Remarks must be at most 500 characters")
    private String remarks;

    /**
     * The interest rate assigned to the approved loan.
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than 0")
    private Double interestRate;

    /**
     * The duration of the loan in months.
     */
    @jakarta.validation.constraints.Min(value = 1, message = "Tenure months must be at least 1")
    @jakarta.validation.constraints.Max(value = 600, message = "Tenure months must be at most 600")
    private Integer tenureMonths;

    /**
     * The final amount sanctioned for the loan.
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Sanctioned amount must be greater than 0")
    private Double sanctionedAmount;
}
