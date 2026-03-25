// DecisionRequest.java
package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

@Data
public class DecisionRequest {
    private String decision;        // APPROVED or REJECTED
    private String remarks;         // Admin note
    private Double interestRate;    // e.g. 8.5
    private Integer tenureMonths;   // e.g. 60
    private Double sanctionedAmount; // e.g. 500000.0
}