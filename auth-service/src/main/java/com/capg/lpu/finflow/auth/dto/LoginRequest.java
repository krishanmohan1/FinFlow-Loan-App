package com.capg.lpu.finflow.auth.dto;

import lombok.Data;

/**
 * Data Transfer Object representing the payload for a user login request.
 * Encapsulates the necessary credentials needed to authenticate against the system.
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}