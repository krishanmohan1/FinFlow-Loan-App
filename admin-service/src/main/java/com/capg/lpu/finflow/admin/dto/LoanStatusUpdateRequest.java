// LoanStatusUpdateRequest.java
package com.capg.lpu.finflow.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanStatusUpdateRequest {
    private String status;
    private String remarks;
}