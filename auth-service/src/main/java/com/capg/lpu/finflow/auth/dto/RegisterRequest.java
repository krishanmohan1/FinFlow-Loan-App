package com.capg.lpu.finflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for user registration requests.
 * Contains the initial credentials for a new user account.
 */
@Data
public class RegisterRequest {
    /**
     * The desired username for the new account.
     */
	@NotBlank(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]{3,29}$",
            message = "Username must start with a letter and contain only letters, numbers, and underscores"
    )
    private String username;

    /**
     * The plain-text password to be encoded and stored.
     */
	@NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;
}
