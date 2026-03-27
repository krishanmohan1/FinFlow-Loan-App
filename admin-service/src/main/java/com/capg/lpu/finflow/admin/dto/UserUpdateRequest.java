package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

/**
 * Data Transfer Object conveying targeted administrative user configuration instructions seamlessly mapping modifications securely.
 */
@Data
public class UserUpdateRequest {

    /**
     * Absolute declarative parameter strictly defining designated application capabilities (e.g., USER or ADMIN).
     */
    private String role;

    /**
     * Express boolean modifier effectively restricting overarching accessibility paths explicitly.
     */
    private Boolean active;
}