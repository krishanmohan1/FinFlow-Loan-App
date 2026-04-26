package com.capg.lpu.finflow.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request model for creating a loan application from external clients.
 * Keeps client-facing validation separate from the persistence entity.
 */
@Data
public class LoanApplicationRequest {

    /**
     * Requested loan amount.
     */
    @DecimalMin(value = "1000.00", message = "Loan amount must be at least 1000")
    private Double amount;

    /**
     * Requested loan category.
     */
    @NotBlank(message = "Loan type is required")
    @Pattern(
            regexp = "^(PERSONAL|HOME|AUTO|EDUCATION|BUSINESS|GOLD)$",
            message = "Loan type must be PERSONAL, HOME, AUTO, EDUCATION, BUSINESS, or GOLD"
    )
    private String loanType;

    /**
     * Requested tenure in months for the loan application.
     */
    @NotNull(message = "Tenure is required")
    @jakarta.validation.constraints.Min(value = 6, message = "Tenure must be at least 6 months")
    @jakarta.validation.constraints.Max(value = 360, message = "Tenure must be at most 360 months")
    private Integer tenureMonths;

    /**
     * Borrower-provided purpose of the loan.
     */
    @NotBlank(message = "Purpose is required")
    @Size(min = 10, max = 255, message = "Purpose must be between 10 and 255 characters")
    private String purpose;
}
