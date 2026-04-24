package com.capg.lpu.finflow.admin.dto;

import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^(USER|ADMIN)$", message = "Role must be USER or ADMIN")
    private String role;

    /**
     * Flag indicating whether the user account is active.
     */
    private Boolean active;
}
