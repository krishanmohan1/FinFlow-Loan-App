package com.capg.lpu.finflow.auth.dto;

/**
 * Internal auth result that carries the access-token response body and the raw refresh token.
 */
public record AuthenticatedSession(AuthResponse response, String refreshToken) {
}
