package com.capg.lpu.finflow.auth.dto;

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
    private String username;

    /**
     * The raw password provided for authentication.
     */
    private String password;
}