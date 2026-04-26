package com.capg.lpu.finflow.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility component responsible for validating and extracting information from JSON Web Tokens (JWT).
 * This class ensures that tokens presented to the API Gateway are legitimate and not expired.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    /**
     * Generates a cryptographic Key object from the predefined secret string.
     *
     * @return the Key object used for HMAC-SHA algorithms
     */
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
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
            Claims claims = extractAllClaims(token);
            boolean valid = issuer.equals(claims.getIssuer())
                    && claims.getSubject() != null
                    && claims.get("role", String.class) != null;
            log.debug("Token is valid: {}", valid);
            return valid;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}
