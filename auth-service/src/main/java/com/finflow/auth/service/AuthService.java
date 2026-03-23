package com.finflow.auth.service;

import com.finflow.auth.dto.AuthResponse;
import com.finflow.auth.dto.LoginRequest;
import com.finflow.auth.dto.RegisterRequest;
import com.finflow.auth.entity.User;
import com.finflow.auth.repository.UserRepository;
import com.finflow.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ✅ Register new user
    public AuthResponse register(RegisterRequest request) {
        log.info("📝 Register attempt for username: {}", request.getUsername());

        // Check duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("⚠️ Username already exists: {}", request.getUsername());
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
        log.info("✅ User registered successfully: {}", user.getUsername());

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole(),
                "Registration successful");
    }

    // ✅ Login existing user
    public AuthResponse login(LoginRequest request) {
        log.info("🔐 Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("❌ User not found: {}", request.getUsername());
                    return new RuntimeException("User not found: " + request.getUsername());
                });

        if (!user.isActive()) {
            log.warn("🚫 Inactive account login attempt: {}", request.getUsername());
            throw new RuntimeException("Account is inactive. Please contact admin.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("❌ Invalid password for user: {}", request.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        log.info("✅ Login successful for user: {}, role: {}", user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getUsername(), user.getRole(), "Login successful");
    }
}