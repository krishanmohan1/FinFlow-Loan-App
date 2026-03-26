package com.capg.lpu.finflow.auth.controller;

import com.capg.lpu.finflow.auth.dto.AuthResponse;
import com.capg.lpu.finflow.auth.dto.LoginRequest;
import com.capg.lpu.finflow.auth.dto.RegisterRequest;
import com.capg.lpu.finflow.auth.dto.UserResponse;
import com.capg.lpu.finflow.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login and user management")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Operation(summary = "Health check")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("GET /auth/test");
        return ResponseEntity.ok("Auth Service is Running");
    }

    @Operation(summary = "Admin health check")
    @GetMapping("/admin/test")
    public ResponseEntity<String> adminTest() {
        log.info("GET /auth/admin/test");
        return ResponseEntity.ok("Admin Access Granted");
    }

    @Operation(summary = "User health check")
    @GetMapping("/user/test")
    public ResponseEntity<String> userTest() {
        log.info("GET /auth/user/test");
        return ResponseEntity.ok("User Access Granted");
    }

    @Operation(summary = "Register a new user", description = "Creates a USER role account and returns a JWT token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("POST /auth/register | username: {}", request.getUsername());
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Login", description = "Validates credentials and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("POST /auth/login | username: {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Get all users", description = "Returns all registered users without passwords")
    @GetMapping("/users/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /auth/users/all");
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /auth/users/{}", id);
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @Operation(summary = "Update user role or active status")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        log.info("PUT /auth/users/{} | role: {} | active: {}", id, request.getRole(), request.getActive());
        return ResponseEntity.ok(authService.updateUser(id, request.getRole(), request.getActive()));
    }

    @Operation(summary = "Deactivate a user account")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        log.info("PUT /auth/users/{}/deactivate", id);
        return ResponseEntity.ok(authService.deactivateUser(id));
    }

    @Data
    static class UpdateUserRequest {
        private String role;
        private Boolean active;
    }
}