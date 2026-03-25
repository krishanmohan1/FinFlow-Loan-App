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

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ----------------------------------------------------------------
    // AUTH — completely unchanged
    // ----------------------------------------------------------------

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

    // ----------------------------------------------------------------
    // USER MANAGEMENT — now returns UserResponse (no password exposed)
    // ----------------------------------------------------------------

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new RuntimeException("User not found with ID: " + id);
                });
        return UserResponse.from(user);
    }

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