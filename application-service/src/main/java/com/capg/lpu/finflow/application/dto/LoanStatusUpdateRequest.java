package com.capg.lpu.finflow.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object for updating the status of a loan application.
 * Used by administrative interfaces to approve or reject loans with optional remarks.
 */
@Data
public class LoanStatusUpdateRequest {

    /**
     * The target status to be applied to the loan application (e.g., APPROVED, REJECTED).
     */
    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "^(PENDING|UNDER_REVIEW|OFFER_MADE|ACTIVE|REJECTED|WITHDRAWN|OFFER_DECLINED)$",
            message = "Status must be PENDING, UNDER_REVIEW, OFFER_MADE, ACTIVE, REJECTED, WITHDRAWN, or OFFER_DECLINED"
    )
    private String status;

    /**
     * Administrative remarks or justification for the status update.
     */
    @Size(max = 500, message = "Remarks must be at most 500 characters")
    private String remarks;

    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than 0")
    private Double interestRate;

    @jakarta.validation.constraints.Min(value = 1, message = "Tenure months must be at least 1")
    @jakarta.validation.constraints.Max(value = 600, message = "Tenure months must be at most 600")
    private Integer tenureMonths;

    @DecimalMin(value = "0.0", inclusive = false, message = "Sanctioned amount must be greater than 0")
    private Double sanctionedAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Processing fee must be greater than 0")
    private Double processingFee;

    @DecimalMin(value = "0.0", inclusive = false, message = "GST amount must be greater than 0")
    private Double gstAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly EMI must be greater than 0")
    private Double monthlyEmi;

    private LocalDate firstEmiDate;

    @Pattern(
            regexp = "^(PENDING|ACCEPTED|DECLINED)$",
            message = "Borrower decision must be PENDING, ACCEPTED, or DECLINED"
    )
    private String borrowerDecision;
}
