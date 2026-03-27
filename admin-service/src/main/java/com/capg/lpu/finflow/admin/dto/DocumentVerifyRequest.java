package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

/**
 * Data Transfer Object for document verification.
 * Contains the administrative status and remarks for a specific document.
 */
@Data
public class DocumentVerifyRequest {

    /**
     * The verification status (e.g., VERIFIED, REJECTED).
     */
    private String status;

    /**
     * Administrative remarks explaining the verification outcome.
     */
    private String remarks;
}