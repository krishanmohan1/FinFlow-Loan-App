package com.capg.lpu.finflow.auth.dto;

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
    private String username;

    /**
     * The plain-text password to be encoded and stored.
     */
    private String password;
}