package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

/**
 * Data Transfer Object dictating necessary administrative decision metrics.
 * Provides granular tracking allocating approval restrictions specifically.
 */
@Data
public class DecisionRequest {

    /**
     * Express decision state marking transition (e.g., APPROVED or REJECTED).
     */
    private String decision;

    /**
     * Discretionary justification annotations provided exclusively evaluating contexts.
     */
    private String remarks;

    /**
     * Assigned structural interest percentages (e.g., 8.5).
     */
    private Double interestRate;

    /**
     * Agreed structural duration spans specifying exact limits (e.g., 60).
     */
    private Integer tenureMonths;

    /**
     * Evaluated and explicitly approved monetary thresholds permitted (e.g., 500000.0).
     */
    private Double sanctionedAmount;
}