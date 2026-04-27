package com.capg.lpu.finflow.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capg.lpu.finflow.auth.dto.AuthResponse;
import com.capg.lpu.finflow.auth.dto.AuthenticatedSession;
import com.capg.lpu.finflow.auth.dto.LoginRequest;
import com.capg.lpu.finflow.auth.dto.ProfileUpdateRequest;
import com.capg.lpu.finflow.auth.dto.RegisterRequest;
import com.capg.lpu.finflow.auth.dto.UserResponse;
import com.capg.lpu.finflow.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for identity and access management.
 * Provides endpoints for user authentication, registration, and administrative user management.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Auth", description = "Registration, login and user management")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Value("${security.jwt.refresh-cookie-name}")
    private String refreshCookieName;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

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
     * 
     */
    
    @Operation(summary = "Register a new user", description = "Creates a USER role account and returns a JWT token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        log.info("POST /auth/register - username: {}", request.getUsername());
        AuthenticatedSession session = authService.register(request);
        attachRefreshCookie(response, session.refreshToken());
        return ResponseEntity.ok(session.response());
    }

    /**
     * Registers a new internal staff/admin account from the secured admin workspace.
     *
     * @param request The staff onboarding details.
     * @return The created admin user profile.
     */
    @Operation(summary = "Register an internal admin account", description = "Creates an ADMIN role account from the secured admin workspace")
    @PostMapping("/admin/register")
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/admin/register - username: {}", request.getUsername());
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    /**
     * Authenticates a user based on provided credentials.
     *
     * @param request The login credentials (username, password).
     * @return An authentication response containing a valid JWT token.
     */

    @Operation(summary = "Login", description = "Validates credentials and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        log.info("POST /auth/login - username: {}", request.getUsername());
        AuthenticatedSession session = authService.login(request);
        attachRefreshCookie(response, session.refreshToken());
        return ResponseEntity.ok(session.response());
    }

    @Operation(summary = "Refresh access token", description = "Uses the HttpOnly refresh-token cookie to rotate the session")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info("POST /auth/refresh");
        AuthenticatedSession session = authService.refreshSession(readRefreshToken(request));
        attachRefreshCookie(response, session.refreshToken());
        return ResponseEntity.ok(session.response());
    }

    @Operation(summary = "Logout", description = "Revokes the current refresh token and clears the browser cookie")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info("POST /auth/logout");
        authService.logout(readRefreshToken(request));
        clearRefreshCookie(response);
        return ResponseEntity.ok("Logout successful");
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
    public ResponseEntity<UserResponse> getUserById(@PathVariable @Positive Long id) {
        log.info("GET /auth/users/{}", id);
        return ResponseEntity.ok(authService.getUserById(id));
    }

    /**
     * Retrieves the authenticated user's own borrower profile.
     *
     * @param username The username injected by the gateway.
     * @return The current user's profile.
     */
    @Operation(summary = "Get current user profile")
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader("X-Auth-Username") String username) {
        log.info("GET /auth/users/me - username: {}", username);
        return ResponseEntity.ok(authService.getCurrentUser(username));
    }

    /**
     * Updates the authenticated user's own borrower profile fields.
     *
     * @param username The username injected by the gateway.
     * @param request The updated borrower profile.
     * @return The updated user profile.
     */
    @Operation(summary = "Update current user profile")
    @PutMapping("/users/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @RequestHeader("X-Auth-Username") String username,
            @Valid @RequestBody ProfileUpdateRequest request) {
        log.info("PUT /auth/users/me - username: {}", username);
        return ResponseEntity.ok(authService.updateCurrentUser(username, request));
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
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateUserRequest request) {
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
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable @Positive Long id) {
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
        @jakarta.validation.constraints.Pattern(
                regexp = "^(USER|ADMIN)$",
                message = "Role must be USER or ADMIN"
        )
        private String role;

        /**
         * The activation status to be set for the account.
         */
        private Boolean active;
    }

    private void attachRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(java.time.Duration.ofMillis(refreshExpirationMs))
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(java.time.Duration.ZERO)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String readRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (refreshCookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
