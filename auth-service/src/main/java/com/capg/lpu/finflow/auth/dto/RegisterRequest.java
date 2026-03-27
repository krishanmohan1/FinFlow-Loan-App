package com.capg.lpu.finflow.auth.dto;

import lombok.Data;

/**
 * Data Transfer Object containing the initial properties for creating a new user account.
 * Requires basic credential assignments which will be further encrypted and stored.
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
}