package com.capg.lpu.finflow.admin.dto;

import lombok.Data;

/**
 * Data Transfer Object for updating user profiles.
 * Allows administrators to modify user roles and activation status.
 */
@Data
public class UserUpdateRequest {

    /**
     * The security role to assign to the user (e.g., USER, ADMIN).
     */
    private String role;

    /**
     * Flag indicating whether the user account is active.
     */
    private Boolean active;
}