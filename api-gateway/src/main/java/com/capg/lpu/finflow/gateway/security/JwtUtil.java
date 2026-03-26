package com.capg.lpu.finflow.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    //  Must be same secret as Auth Service
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Extract all claims at once — avoids parsing token multiple times
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        String username = extractAllClaims(token).getSubject();
        log.debug(" Extracted username from token: {}", username);
        return username;
    }

    public String extractRole(String token) {
        String role = extractAllClaims(token).get("role", String.class);
        log.debug(" Extracted role from token: {}", role);
        return role;
    }

    public boolean validateToken(String token) {
        try {
            // Parsing itself validates signature + expiry
            extractAllClaims(token);
            log.debug(" Token is valid");
            return true;
        } catch (Exception e) {
            log.warn(" Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}