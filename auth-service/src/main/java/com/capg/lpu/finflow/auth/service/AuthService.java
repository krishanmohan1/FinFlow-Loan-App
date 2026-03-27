package com.capg.lpu.finflow.auth.service;

import com.capg.lpu.finflow.auth.dto.AuthResponse;
import com.capg.lpu.finflow.auth.dto.LoginRequest;
import com.capg.lpu.finflow.auth.dto.RegisterRequest;
import com.capg.lpu.finflow.auth.dto.UserResponse;
import com.capg.lpu.finflow.auth.entity.User;
import com.capg.lpu.finflow.auth.repository.UserRepository;
import com.capg.lpu.finflow.auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service orchestrating authentication flows and persistent user management interactions.
 * Central authority on authorization logic execution and profile modifications across the system.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Executes robust business logic verifying initial account registrations to securely persist user accounts.
     * Validates uniqueness prior to cryptographic allocation safely constructing the required database models.
     *
     * @param request encapsulates structural required credential boundaries
     * @return correctly configured authorization responses wrapping validated dynamic tokens
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Register attempt for username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already taken: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole(),
                "Registration successful");
    }

    /**
     * Scrutinizes presented logging parameters, enforcing strong access controls effectively
     * managing activation variables alongside cryptographic signature alignments.
     *
     * @param request contextual credentials passed inherently
     * @return explicitly authorized response embedding actionable validation signatures
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getUsername());
                    return new RuntimeException("User not found: " + request.getUsername());
                });

        if (!user.isActive()) {
            log.warn("Inactive account login attempt: {}", request.getUsername());
            throw new RuntimeException("Account is inactive. Please contact admin.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        log.info("Login successful for user: {}, role: {}", user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getUsername(), user.getRole(), "Login successful");
    }

    /**
     * Systematically aggregates a mapped dataset containing user records stripped of strict sensitive identifiers.
     *
     * @return collection dynamically mapping persisted relational user models onto independent response representations
     */
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Exposes single explicit profile datasets isolating targeted users explicitly mapping required details.
     *
     * @param id identifying persistent database unique sequence record
     * @return mapped details conforming safely to architectural interfaces
     */
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new RuntimeException("User not found with ID: " + id);
                });
        return UserResponse.from(user);
    }

    /**
     * Processes transactional mutations securely adapting authorization boundaries enforcing explicit logical checks.
     *
     * @param id precise target relational identifier
     * @param role hierarchical scope intended for update
     * @param active boolean switch governing system accessibility
     * @return resultant database representation immediately preceding persistence changes mapped successfully
     */
    public UserResponse updateUser(Long id, String role, Boolean active) {
        log.info("Updating user ID: {} | role: {} | active: {}", id, role, active);

        // Fetch the raw User entity from DB (not DTO) so we can update it
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (role != null && !role.isBlank()) {
            if (!"USER".equals(role) && !"ADMIN".equals(role)) {
                throw new IllegalArgumentException("Role must be USER or ADMIN");
            }
            user.setRole(role);
        }

        if (active != null) {
            user.setActive(active);
        }

        User updated = userRepository.save(user);
        log.info("User ID: {} updated successfully", id);
        return UserResponse.from(updated);
    }

    /**
     * Explicitly revokes overarching application clearance for target persisting profiles independently processing restrictions.
     *
     * @param id sequence tracker defining targeted entity bounds
     * @return completely restricted, valid persisting mapped snapshot
     */
    public UserResponse deactivateUser(Long id) {
        log.info("Deactivating user ID: {}", id);

        // Fetch the raw User entity from DB (not DTO) so we can update it
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setActive(false);
        User updated = userRepository.save(user);
        log.info("User ID: {} deactivated", id);
        return UserResponse.from(updated);
    }
}