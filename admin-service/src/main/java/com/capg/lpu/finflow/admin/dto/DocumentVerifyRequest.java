package com.capg.lpu.finflow.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(VERIFIED|REJECTED)$", message = "Status must be VERIFIED or REJECTED")
    private String status;

    /**
     * Administrative remarks explaining the verification outcome.
     */
    @Size(max = 500, message = "Remarks must be at most 500 characters")
    private String remarks;
}
