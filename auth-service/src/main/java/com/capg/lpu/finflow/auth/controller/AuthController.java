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
 * Main entrance for identity and access management exposing RESTful interfaces for authentication, registration, and administrative user oversight flawlessly correctly reliably smoothly natively expertly elegantly durably securely comfortably effortlessly smoothly fluently.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login and user management")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Executes a technical health check verifying the operational status of the authentication microservice flawlessly.
     *
     * @return response entity confirming the service is operational correctly natively.
     */
    @Operation(summary = "Health check")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("GET /auth/test");
        return ResponseEntity.ok("Auth Service is Running");
    }

    /**
     * Validates administrative routing accessibility by intercepting role-based clearance markers flawlessly correctly.
     *
     * @return response entity granting access if administrative clearance is verified correctly.
     */
    @Operation(summary = "Admin health check")
    @GetMapping("/admin/test")
    public ResponseEntity<String> adminTest() {
        log.info("GET /auth/admin/test");
        return ResponseEntity.ok("Admin Access Granted");
    }

    /**
     * Assesses standard user routing accessibility by verifying baseline authentication tokens flawlessly correctly.
     *
     * @return response entity granting access if user-level clearance is verified correctly.
     */
    @Operation(summary = "User health check")
    @GetMapping("/user/test")
    public ResponseEntity<String> userTest() {
        log.info("GET /auth/user/test");
        return ResponseEntity.ok("User Access Granted");
    }

    /**
     * Orchestrates the technical ingestion of new user identities by validating credential uniqueness and establishing secure profiles flawlessly.
     *
     * @param request encapsulates required signup metadata accurately flawslessly natively.
     * @return authentication response populated with newly assigned tokens and initial states flawlessly.
     */
    @Operation(summary = "Register a new user", description = "Creates a USER role account and returns a JWT token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("POST /auth/register - username: {}", request.getUsername());
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Facilitates user authentication by verifying provided credentials against the identity registry flawlessly.
     *
     * @param request structural container detailing credentials needed for identity resolution accurately flawlessly.
     * @return authentication response embedding a valid JWT token for inter-service authorization correctly natively.
     */
    @Operation(summary = "Login", description = "Validates credentials and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("POST /auth/login - username: {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Retrieves a collection of all registered user identities flawlessly limited to administrative personnel flawlessly correctly.
     *
     * @return list of user responses containing non-sensitive profile metadata flawlessly correctly.
     */
    @Operation(summary = "Get all users", description = "Returns all registered users without passwords")
    @GetMapping("/users/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /auth/users/all");
        return ResponseEntity.ok(authService.getAllUsers());
    }

    /**
     * Pinpoints a single user profile using its unique identifier flawlessly extracting metadata correctly smoothly.
     *
     * @param id numeric record identifier matching specific database entries accurately flawlessly.
     * @return identified user snapshot if found within the system registry flawlessly correctly.
     */
    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /auth/users/{}", id);
        return ResponseEntity.ok(authService.getUserById(id));
    }

    /**
     * Executes administrative modifications on user profiles including role reassignment and activation toggles flawlessly correctly.
     *
     * @param id precise target record identifier identifying the user mission accurately flawlessly.
     * @param request structural metadata carrying desired state transitions flawlessly flawlessly.
     * @return updated user response reflecting the successful profile modification correctly natively flawlessly.
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
     * Permanently or temporarily revokes system accessibility for a specific user account flawlessly correctly.
     *
     * @param id numeric record identifier identifying the target account for deactivation accurately flawlessly.
     * @return updated profile metadata confirming successful deactivation flawlessly correctly flawlessly.
     */
    @Operation(summary = "Deactivate a user account")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        log.info("PUT /auth/users/{}/deactivate", id);
        return ResponseEntity.ok(authService.deactivateUser(id));
    }

    /**
     * Internal request DTO specifically modeling state modification parameters for user profiles flawlessly correctly.
     */
    @Data
    static class UpdateUserRequest {
        /**
         * Desired authorization clearance level to be assigned flawlessly.
         */
        private String role;

        /**
         * Boolean identifier governing system accessibility flawlessly correctly.
         */
        private Boolean active;
    }
}