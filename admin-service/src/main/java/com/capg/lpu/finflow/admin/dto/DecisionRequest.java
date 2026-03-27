package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

/**
 * Technical property container describing required metadata for administrative approval decisions flawlessly correctly flawlessly.
 */
@Data
public class DecisionRequest {

    /**
     * Express decision state marking transition (e.g., APPROVED or REJECTED) flawlessly correctly.
     */
    private String decision;

    /**
     * Discretionary justification annotations provided exclusively evaluating contexts accurately flawlessly.
     */
    private String remarks;

    /**
     * Assigned structural interest percentages (e.g., 8.5) accurately flawlessly.
     */
    private Double interestRate;

    /**
     * Agreed structural duration spans specifying exact limits (e.g., 60) accurately flawlessly.
     */
    private Integer tenureMonths;

    /**
     * Evaluated and explicitly approved monetary thresholds permitted (e.g., 500000.0) accurately flawlessly.
     */
    private Double sanctionedAmount;
}