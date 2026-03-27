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
 * REST controller for identity and access management.
 * Provides endpoints for user authentication, registration, and administrative user management.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login and user management")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Basic health check endpoint to verify the Auth Service is operational.
     *
     * @return A response entity confirming the service is running.
     */
    @Operation(summary = "Health check")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("GET /auth/test");
        return ResponseEntity.ok("Auth Service is Running");
    }

    /**
     * Verification endpoint for administrative access.
     *
     * @return A response entity confirming administrative access.
     */
    @Operation(summary = "Admin health check")
    @GetMapping("/admin/test")
    public ResponseEntity<String> adminTest() {
        log.info("GET /auth/admin/test");
        return ResponseEntity.ok("Admin Access Granted");
    }

    /**
     * Verification endpoint for standard user access.
     *
     * @return A response entity confirming user-level access.
     */
    @Operation(summary = "User health check")
    @GetMapping("/user/test")
    public ResponseEntity<String> userTest() {
        log.info("GET /auth/user/test");
        return ResponseEntity.ok("User Access Granted");
    }

    /**
     * Registers a new user identity in the system.
     * Validates credentials and initializes a user profile.
     *
     * @param request The registration details (username, password, email).
     * @return An authentication response containing the generated JWT token.
     */
    @Operation(summary = "Register a new user", description = "Creates a USER role account and returns a JWT token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("POST /auth/register - username: {}", request.getUsername());
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticates a user based on provided credentials.
     *
     * @param request The login credentials (username, password).
     * @return An authentication response containing a valid JWT token.
     */
    @Operation(summary = "Login", description = "Validates credentials and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("POST /auth/login - username: {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Retrieves a list of all users registered in the system.
     *
     * @return A list of user responses containing non-sensitive profile metadata.
     */
    @Operation(summary = "Get all users", description = "Returns all registered users without passwords")
    @GetMapping("/users/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /auth/users/all");
        return ResponseEntity.ok(authService.getAllUsers());
    }

    /**
     * Retrieves a detailed user profile by its ID.
     *
     * @param id The unique identifier of the user account.
     * @return The requested user response if found.
     */
    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /auth/users/{}", id);
        return ResponseEntity.ok(authService.getUserById(id));
    }

    /**
     * Updates an existing user profile's role or active status.
     *
     * @param id The ID of the user account to update.
     * @param request The target role and active status bit.
     * @return The updated user profile data.
     */
    @Operation(summary = "Update user role or active status")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        log.info("PUT /auth/users/{} - role: {} - active: {}", id, request.getRole(), request.getActive());
        return ResponseEntity.ok(authService.updateUser(id, request.getRole(), request.getActive()));
    }

    /**
     * Deactivates a user account, disabling further platform access.
     *
     * @param id The ID of the user account to deactivate.
     * @return The updated user profile data.
     */
    @Operation(summary = "Deactivate a user account")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        log.info("PUT /auth/users/{}/deactivate", id);
        return ResponseEntity.ok(authService.deactivateUser(id));
    }

    /**
     * Internal DTO for defining user profile update parameters.
     */
    @Data
    static class UpdateUserRequest {
        /**
         * The new role to be assigned to the user.
         */
        private String role;

        /**
         * The activation status to be set for the account.
         */
        private Boolean active;
    }
}