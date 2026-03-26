package com.capg.lpu.finflow.application.dto;

import lombok.Data;

@Data
public class LoanStatusUpdateRequest {
    private String status;
    private String remarks;
}