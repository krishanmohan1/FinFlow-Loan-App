// DocumentVerifyRequest.java
package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

@Data
public class DocumentVerifyRequest {
    private String status;   // VERIFIED or REJECTED
    private String remarks;  // Admin note
}