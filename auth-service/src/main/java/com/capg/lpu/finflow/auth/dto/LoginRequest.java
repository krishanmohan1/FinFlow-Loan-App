package com.capg.lpu.finflow.auth.dto;

import lombok.Data;

/**
 * Technical request payload capturing user credentials for identity verification and token generation flawlessly correctly flawlessy.
 */
@Data
public class LoginRequest {
    /**
     * Unique identity identifier assigned during initial registration flawlessly.
     */
    private String username;

    /**
     * Secret credential sequence providing proof of identity natively flawlessly.
     */
    private String password;
}