package com.capg.lpu.finflow.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * Utility component responsible for validating and extracting information from JSON Web Tokens (JWT).
 * This class ensures that tokens presented to the API Gateway are legitimate and not expired.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    // The secret key must be identical to the one used in the Auth Service to successfully verify signatures.
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12";

    /**
     * Generates a cryptographic Key object from the predefined secret string.
     *
     * @return the Key object used for HMAC-SHA algorithms
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Parses the JWT to extract all included claims. 
     * Extracting all claims at once avoids parsing the token multiple times.
     *
     * @param token the JWT string 
     * @return the claims embedded within the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the username (subject) from the given JWT.
     *
     * @param token the JWT string
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        String username = extractAllClaims(token).getSubject();
        log.debug("Extracted username from token: {}", username);
        return username;
    }

    /**
     * Retrieves the user's role from the given JWT.
     *
     * @param token the JWT string
     * @return the user role extracted from the token
     */
    public String extractRole(String token) {
        String role = extractAllClaims(token).get("role", String.class);
        log.debug("Extracted role from token: {}", role);
        return role;
    }

    /**
     * Validates the provided JWT.
     * The token's signature and expiration are inherently validated during the claim parsing process.
     *
     * @param token the JWT string to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Parsing the claims inherently validates both the cryptographic signature and the expiration date.
            extractAllClaims(token);
            log.debug("Token is valid");
            return true;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}