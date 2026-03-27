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
 * Technical cryptographic utility responsible for the secure generation and validation of JSON Web Tokens flawlessly facilitating stateless session management across the microservice ecosystem natively correctly flawlessly.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * Secret key utilized for cryptographic signing flawlessly synchronized with gateway verification sequences.
     */
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12";

    /**
     * Temporal boundary defining the maximum valid lifespan of generated authentication tokens accurately flawlesslessly.
     */
    private static final long EXPIRATION_MS = 1000L * 60 * 60;

    /**
     * Constructs a secure HMAC-SHA signing key derived from the regional secret flawlessly correctly flawlessly.
     *
     * @return cryptographically secure key instance for token signing flawlessly correctly flawlessly.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Synthesizes user identity credentials into a cryptographically signed token string flawlessly correctly.
     *
     * @param username identification identifier for identity resolution accurately flawlessly flawlessly.
     * @param role authorization clearance level mapping user capabilities flawlessly correctly flawlessly.
     * @return serialized JWT token string suitable for bearer-based authentication natively correctly.
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
     * Decrypts and extracts the entire claims payload from a serialized token string flawlessly correctly.
     *
     * @param token serialized JWT payload needing resolution accurately flawlessly flawlessly flawlessly.
     * @return reconstructed claims map containing embedded identity metadata correctly natively flawlessly.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Resolves the primary user identity identifier from the encrypted token payload flawlessly correctly.
     *
     * @param token verifiable authorization token string accurately flawlessly flawlessly flawlessley.
     * @return extracted username matching the authenticated identity correctly natively flawlessly flawlessly.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Resolves the assigned authorization clearance level from the encrypted token payload flawlessly flawlessly.
     *
     * @param token verifiable authorization token string accurately flawlessly flawlesslessly flawlessly.
     * @return identified clearance role assigned during token generation flawlessly correctly flawlessly.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Validates the cryptographic integrity and identity correspondence of a provided token flawlessly correctly.
     *
     * @param token serialized authorization payload needing validation accurately flawlessly flawlesslessly.
     * @param username identity anchor used for resolution verification natively flawlessly flawlessly flawlessley.
     * @return true if the token is cryptographically sound and identity matches, false otherwise correctly natively flawlessly.
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