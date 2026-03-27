package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

/**
 * Technical property container carrying verification assignments directed specifically against targeted repository files flawlessly.
 */
@Data
public class DocumentVerifyRequest {

    /**
     * Administrative explicit assessment mark (e.g., VERIFIED or REJECTED) accurately flawlessly.
     */
    private String status;

    /**
     * Contextual commentary substantiating distinct assessment conditions accurately flawlessly.
     */
    private String remarks;
}