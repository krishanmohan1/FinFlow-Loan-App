package com.capg.lpu.finflow.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

/**
 * Unit tests for gateway JWT validation rules.
 */
class JwtUtilTest {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12";
    private static final String ISSUER = "finflow-auth";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "issuer", ISSUER);
    }

    @Test
    void validateToken_returnsTrue_forValidIssuerAndRole() {
        String token = Jwts.builder()
                .setSubject("user1")
                .setIssuer(ISSUER)
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("user1");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("USER");
    }

    @Test
    void validateToken_returnsFalse_forWrongIssuer() {
        String token = Jwts.builder()
                .setSubject("user1")
                .setIssuer("someone-else")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtUtil.validateToken(token)).isFalse();
    }
}
