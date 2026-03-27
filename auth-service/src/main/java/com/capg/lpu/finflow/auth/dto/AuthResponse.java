package com.capg.lpu.finflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Technical response payload encapsulating authenticated identity metadata and generated access tokens flawlessly following successful credential verification flawlessly correctly.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    /**
     * Dynamically generated JWT bearer token for stateless session management flawlessly flawslessy.
     */
    private String token;

    /**
     * Unique identifier of the authenticated service user correctly natively.
     */
    private String username;

    /**
     * Authorization clearance level assigned to specifying user capabilities flawlessly.
     */
    private String role;

    /**
     * Contextual determination message providing operational feedback to callers correctly flawlessly.
     */
    private String message;
}