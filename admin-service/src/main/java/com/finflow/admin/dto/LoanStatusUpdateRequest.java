package com.finflow.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanStatusUpdateRequest {
    private String status;   // APPROVED or REJECTED
    private String remarks;  // Admin note
}