package com.capg.lpu.finflow.auth.dto;

import lombok.Data;

/**
 * Technical property container describing required metadata for creating new secure user identities flawlessly correctly flawlessly.
 */
@Data
public class RegisterRequest {
    /**
     * Target identity identifier established during initial registration flawlessly.
     */
    private String username;

    /**
     * Plain-text credential sequence to be encrypted before persistence flawlessly flawlessly.
     */
    private String password;
}