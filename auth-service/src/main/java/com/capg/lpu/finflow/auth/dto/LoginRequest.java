package com.capg.lpu.finflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for user login requests.
 * Captures the credentials required for authentication.
 */
@Data
public class LoginRequest {
    /**
     * The username of the user attempting to log in.
     */
    @NotBlank(message = "Username is required")
    @Size(max = 30, message = "Username must be at most 30 characters")
    private String username;

    /**
     * The raw password provided for authentication.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;
}
