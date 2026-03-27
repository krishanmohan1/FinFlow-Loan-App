package com.capg.lpu.finflow.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object modeling straightforward transitional updates tracking explicit status changes efficiently.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanStatusUpdateRequest {

    /**
     * Defining categorical state mapping variable.
     */
    private String status;

    /**
     * Additional notes explaining explicit status progression context mapping properly.
     */
    private String remarks;
}