package com.capg.lpu.finflow.application.dto;

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
    private String status;

    /**
     * Administrative remarks or justification for the status update.
     */
    private String remarks;
}