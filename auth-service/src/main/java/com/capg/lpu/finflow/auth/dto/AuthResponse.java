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
     * The short-lived access token for authenticated API requests.
     */
    private String accessToken;

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

    /**
     * Access-token lifetime in milliseconds.
     */
    private long accessTokenExpiresInMs;
}
