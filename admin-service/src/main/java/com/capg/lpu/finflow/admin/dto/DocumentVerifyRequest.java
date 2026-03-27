package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

/**
 * Data Transfer Object carrying verification assignments directed specifically against targeted repository files.
 */
@Data
public class DocumentVerifyRequest {

    /**
     * Administrative explicit assessment mark (e.g., VERIFIED or REJECTED).
     */
    private String status;

    /**
     * Contextual commentary substantiating distinct assessment conditions.
     */
    private String remarks;
}