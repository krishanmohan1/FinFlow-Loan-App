package com.finflow.auth.controller;

import com.finflow.auth.dto.AuthResponse;
import com.finflow.auth.dto.LoginRequest;
import com.finflow.auth.dto.RegisterRequest;
import com.finflow.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    // Health check
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("GET /auth/test");
        return ResponseEntity.ok("✅ Auth Service is Running");
    }

    // Test endpoints for role verification via Gateway
    @GetMapping("/admin/test")
    public ResponseEntity<String> adminTest() {
        log.info("GET /auth/admin/test → ADMIN access confirmed");
        return ResponseEntity.ok("✅ Admin Access Granted");
    }

    @GetMapping("/user/test")
    public ResponseEntity<String> userTest() {
        log.info("GET /auth/user/test → USER access confirmed");
        return ResponseEntity.ok("✅ User Access Granted");
    }

    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("POST /auth/register → username: {}", request.getUsername());
        return ResponseEntity.ok(authService.register(request));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("POST /auth/login → username: {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }
}