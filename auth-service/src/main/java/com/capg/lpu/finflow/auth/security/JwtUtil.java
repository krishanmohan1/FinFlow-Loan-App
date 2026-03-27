package com.capg.lpu.finflow.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Responsible for securely constructing and validating authentication tokens.
 * Interoperates directly with the overarching API Gateway security constraints.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * This secret relies on parity with downstream services to allow distributed claim readings.
     */
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12";

    /**
     * Valid threshold window governing JWT lifespans securely assigned dynamically upon token issue.
     */
    private static final long EXPIRATION_MS = 1000L * 60 * 60;

    /**
     * Safely constructs cryptographic credentials utilized to consistently sign outgoing token strings.
     *
     * @return Key utilizing secure hashing algorithms compatible with JWT.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Synthesizes user credentials into an encrypted token containing required sub-system authorizations.
     *
     * @param username identification credential for system targeting
     * @param role authorized roles mapped specifically avoiding raw string errors
     * @return encoded, fully actionable Bearer token
     */
    public String generateToken(String username, String role) {
        log.debug("Generating token for user: {}, role: {}", username, role);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Reconstitutes embedded metrics directly parsing cryptographic hashes efficiently.
     *
     * @param token actionable JSON structured string
     * @return securely validated object array mapped to claim descriptors
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Single responsibility method focusing completely traversing payload specifically looking for subjects.
     *
     * @param token verifiable JSON payload structure
     * @return extracted primary username
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Single responsibility method focusing completely traversing payload looking for authorized clearance maps.
     *
     * @param token verifiable JSON payload structure
     * @return verified clearance tier assignments
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Safely assures provided token legitimacy by intercepting parsing faults and verifying usernames.
     *
     * @param token string requiring comprehensive legitimacy assessment
     * @param username cross verification anchor constraint securely assigned previously
     * @return true explicitly verifying the entire sequence, false if discrepancies exist
     */
    public boolean validateToken(String token, String username) {
        try {
            String extracted = extractUsername(token);
            boolean valid = extracted.equals(username);
            log.debug("Token validation for user {}: {}", username, valid);
            return valid;
        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            return false;
        }
    }
}