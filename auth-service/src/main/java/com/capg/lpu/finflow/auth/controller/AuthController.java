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

/**
 * REST controller responsible for handling authentication, registration,
 * and user entity management.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login and user management")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Checks the general health of the authentication service.
     *
     * @return a simple status message indicating the service is running
     */
    @Operation(summary = "Health check")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("GET /auth/test");
        return ResponseEntity.ok("Auth Service is Running");
    }

    /**
     * Validates administrative routing access. 
     * Requires ADMIN roles strictly from the API Gateway filtering.
     *
     * @return a confirmation message indicating admin privileges
     */
    @Operation(summary = "Admin health check")
    @GetMapping("/admin/test")
    public ResponseEntity<String> adminTest() {
        log.info("GET /auth/admin/test");
        return ResponseEntity.ok("Admin Access Granted");
    }

    /**
     * Validates general user routing access.
     * Requires authenticated USER roles natively checked by API Gateway.
     *
     * @return a confirmation message indicating user privileges
     */
    @Operation(summary = "User health check")
    @GetMapping("/user/test")
    public ResponseEntity<String> userTest() {
        log.info("GET /auth/user/test");
        return ResponseEntity.ok("User Access Granted");
    }

    /**
     * Processes new user registration requests. 
     * Creates an inactive profile strictly managed initially.
     *
     * @param request encapsulates the necessary user details for sign-up
     * @return an authentication response populated with newly assigned tokens and states
     */
    @Operation(summary = "Register a new user", description = "Creates a USER role account and returns a JWT token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("POST /auth/register | username: {}", request.getUsername());
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticates existing users based on provided credentials.
     *
     * @param request the structured object detailing username and strictly parsed passwords
     * @return the resulting JSON string token for sub-service operations authorization
     */
    @Operation(summary = "Login", description = "Validates credentials and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("POST /auth/login | username: {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Fetches details of all current active and inactive users across the platform.
     * Sensitive attributes like passwords are obfuscated within UserResponse maps.
     *
     * @return a collection of user information entities
     */
    @Operation(summary = "Get all users", description = "Returns all registered users without passwords")
    @GetMapping("/users/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /auth/users/all");
        return ResponseEntity.ok(authService.getAllUsers());
    }

    /**
     * Retrieves specific properties allocated to a recognized unique identifier.
     *
     * @param id the core identification metric of the user profile
     * @return the singular verified user snapshot
     */
    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /auth/users/{}", id);
        return ResponseEntity.ok(authService.getUserById(id));
    }

    /**
     * Adjusts the current status metrics specifically isolating roles and active constraints.
     * Used typically via an Admin panel functionality to unlock users.
     *
     * @param id the system footprint tag of user
     * @param request the mutable constraints intended for integration
     * @return the formally saved resulting metadata
     */
    @Operation(summary = "Update user role or active status")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        log.info("PUT /auth/users/{} | role: {} | active: {}", id, request.getRole(), request.getActive());
        return ResponseEntity.ok(authService.updateUser(id, request.getRole(), request.getActive()));
    }

    /**
     * Toggles the system accessibility for given accounts, rendering them temporarily or permanently unusable.
     *
     * @param id internal numerical reference
     * @return details defining updated deactivation results
     */
    @Operation(summary = "Deactivate a user account")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        log.info("PUT /auth/users/{}/deactivate", id);
        return ResponseEntity.ok(authService.deactivateUser(id));
    }

    /**
     * Internal data transfer object specifically modeling inline updates for
     * user state changes.
     */
    @Data
    static class UpdateUserRequest {
        private String role;
        private Boolean active;
    }
}