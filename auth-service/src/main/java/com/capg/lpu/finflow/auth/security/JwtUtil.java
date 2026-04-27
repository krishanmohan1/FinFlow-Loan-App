package com.capg.lpu.finflow.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for JSON Web Token (JWT) operations.
 * Handles the generation, parsing, and validation of JWTs for stateless authentication.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Generates a signing key from the configured secret.
     *
     * @return A Key object for HMAC-SHA signing.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for a given user and their associated role.
     *
     * @param username The username of the authenticated user.
     * @param role The security role assigned to the user.
     * @return A compact, URL-safe JWT string.
     */
    public String generateToken(String username, String role) {
        log.debug("Generating token for user: {}, role: {}", username, role);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setIssuer(issuer)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessTokenExpirationMs() {
        return expirationMs;
    }

    /**
     * Parses the JWT token and extracts all contained claims.
     *
     * @param token The serialized JWT string.
     * @return The Claims object containing token metadata.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the subject (username) from the token.
     *
     * @param token The serialized JWT string.
     * @return The username embedded in the token.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts the security role claim from the token.
     *
     * @param token The serialized JWT string.
     * @return The role string embedded in the token.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Validates if a token matches the expected user and is cryptographically sound.
     *
     * @param token The serialized JWT string.
     * @param username The expected username.
     * @return true if the token is valid and belongs to the user, false otherwise.
     */
    public boolean validateToken(String token, String username) {
        try {
            Claims claims = extractAllClaims(token);
            String extracted = claims.getSubject();
            boolean valid = extracted.equals(username) && issuer.equals(claims.getIssuer());
            log.debug("Token validation for user {}: {}", username, valid);
            return valid;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token validation error: {}", e.getMessage());
            return false;
        }
    }
}
