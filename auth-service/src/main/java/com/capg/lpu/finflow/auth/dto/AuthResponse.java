package com.capg.lpu.finflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for authentication responses.
 * Carries the JWT token and user profile metadata after successful login or registration.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    /**
     * The generated JWT token for subsequent authenticated requests.
     */
    private String token;

    /**
     * The username of the authenticated user.
     */
    private String username;

    /**
     * The security role assigned to the user.
     */
    private String role;

    /**
     * A descriptive message regarding the authentication result.
     */
    private String message;
}