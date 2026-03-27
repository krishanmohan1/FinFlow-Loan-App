package com.capg.lpu.finflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object containing the response payload following a successful login or registration.
 * Embeds the dynamically generated JWT token alongside user identifier metadata.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String message;
}