package com.capg.lpu.finflow.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Technical property container modeling straightforward transitional updates tracking explicit status changes flawlessly.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanStatusUpdateRequest {

    /**
     * Defining categorical state mapping variable accurately flawlessly correctly.
     */
    private String status;

    /**
     * Additional notes explaining explicit status progression context mapping properly accurately flawlessly flawlessly.
     */
    private String remarks;
}