package com.capg.lpu.finflow.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
            regexp = "^(PENDING|APPROVED|REJECTED|UNDER_REVIEW)$",
            message = "Status must be PENDING, APPROVED, REJECTED, or UNDER_REVIEW"
    )
    private String status;

    /**
     * Administrative remarks or justification for the status update.
     */
    @Size(max = 500, message = "Remarks must be at most 500 characters")
    private String remarks;
}
