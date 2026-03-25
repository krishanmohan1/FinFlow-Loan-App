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

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    // ⚠️ Must match API Gateway secret exactly
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12";

    // Token valid for 1 hour
    private static final long EXPIRATION_MS = 1000L * 60 * 60;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 🔥 Generate token with username + role
    public String generateToken(String username, String role) {
        log.debug("🔑 Generating token for user: {}, role: {}", username, role);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token, String username) {
        try {
            String extracted = extractUsername(token);
            boolean valid = extracted.equals(username);
            log.debug("🔍 Token validation for user {}: {}", username, valid);
            return valid;
        } catch (Exception e) {
            log.warn("❌ Token validation error: {}", e.getMessage());
            return false;
        }
    }
}