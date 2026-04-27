package com.capg.lpu.finflow.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capg.lpu.finflow.auth.dto.AuthResponse;
import com.capg.lpu.finflow.auth.dto.AuthenticatedSession;
import com.capg.lpu.finflow.auth.dto.LoginRequest;
import com.capg.lpu.finflow.auth.dto.ProfileUpdateRequest;
import com.capg.lpu.finflow.auth.dto.RegisterRequest;
import com.capg.lpu.finflow.auth.dto.UserResponse;
import com.capg.lpu.finflow.auth.entity.RefreshToken;
import com.capg.lpu.finflow.auth.entity.User;
import com.capg.lpu.finflow.auth.repository.RefreshTokenRepository;
import com.capg.lpu.finflow.auth.repository.UserRepository;
import com.capg.lpu.finflow.auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for handling authentication, rotating refresh tokens, and user management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    /**
     * Registers a new borrower and opens a session.
     */
    @Transactional
    public AuthenticatedSession register(RegisterRequest request) {
        log.info("Register attempt for username: {}", request.getUsername());
        User user = createUser(request, "USER");
        log.info("User registered successfully: {}", user.getUsername());
        return openSession(user, "Registration successful");
    }

    /**
     * Creates an internal admin account from the admin workspace flow.
     */
    @Transactional
    public UserResponse registerAdmin(RegisterRequest request) {
        log.info("Admin onboarding attempt for username: {}", request.getUsername());
        User user = createUser(request, "ADMIN");
        log.info("Admin account created successfully: {}", user.getUsername());
        return UserResponse.from(user);
    }

    /**
     * Authenticates a user and opens a fresh rotating session.
     */
    @Transactional
    public AuthenticatedSession login(LoginRequest request) {
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

        log.info("Login successful for user: {}, role: {}", user.getUsername(), user.getRole());
        return openSession(user, "Login successful");
    }

    /**
     * Uses a valid refresh token to rotate the session and mint a new access token.
     */
    @Transactional
    public AuthenticatedSession refreshSession(String rawRefreshToken) {
        RefreshToken persistedToken = findActiveRefreshToken(rawRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token is invalid or expired"));

        if (persistedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            persistedToken.setRevoked(true);
            refreshTokenRepository.save(persistedToken);
            throw new RuntimeException("Refresh token has expired");
        }

        User user = persistedToken.getUser();
        if (!user.isActive()) {
            persistedToken.setRevoked(true);
            refreshTokenRepository.save(persistedToken);
            throw new RuntimeException("Account is inactive. Please contact admin.");
        }

        persistedToken.setRevoked(true);
        persistedToken.setLastUsedAt(LocalDateTime.now());
        refreshTokenRepository.save(persistedToken);

        return openSession(user, "Session refreshed");
    }

    /**
     * Revokes the supplied refresh token if it exists.
     */
    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }

        findActiveRefreshToken(rawRefreshToken).ifPresent(token -> {
            token.setRevoked(true);
            token.setLastUsedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
            log.info("Refresh token revoked for user: {}", token.getUser().getUsername());
        });
    }

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

    public UserResponse getCurrentUser(String username) {
        log.info("Fetching current user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(String username, ProfileUpdateRequest request) {
        log.info("Updating current user profile for username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddressLine1(request.getAddressLine1());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPostalCode(request.getPostalCode());
        user.setOccupation(request.getOccupation());
        user.setAnnualIncome(request.getAnnualIncome());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, String role, Boolean active) {
        log.info("Updating user ID: {} - role: {} - active: {}", id, role, active);

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
            if (!active) {
                revokeAllActiveTokens(user);
            }
        }

        User updated = userRepository.save(user);
        log.info("User ID: {} updated successfully", id);
        return UserResponse.from(updated);
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        log.info("Deactivating user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setActive(false);
        revokeAllActiveTokens(user);
        User updated = userRepository.save(user);
        log.info("User ID: {} deactivated", id);
        return UserResponse.from(updated);
    }

    private AuthenticatedSession openSession(User user, String message) {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
        String rawRefreshToken = generateRefreshTokenValue();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawRefreshToken))
                .expiresAt(LocalDateTime.now().plus(java.time.Duration.ofMillis(refreshExpirationMs)))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        AuthResponse response = new AuthResponse(
                accessToken,
                user.getUsername(),
                user.getRole(),
                message,
                jwtUtil.getAccessTokenExpirationMs()
        );
        return new AuthenticatedSession(response, rawRefreshToken);
    }

    private User createUser(RegisterRequest request, String role) {
        ensureUniqueIdentity(request.getUsername(), request.getEmail());

        User user = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .addressLine1(request.getAddressLine1())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .occupation(request.getOccupation())
                .annualIncome(request.getAnnualIncome())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        return userRepository.save(user);
    }

    private void ensureUniqueIdentity(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            log.warn("Username already exists: {}", username);
            throw new RuntimeException("Username already taken: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Email already exists: {}", email);
            throw new RuntimeException("Email already registered: " + email);
        }
    }

    private Optional<RefreshToken> findActiveRefreshToken(String rawRefreshToken) {
        return refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(rawRefreshToken));
    }

    private void revokeAllActiveTokens(User user) {
        refreshTokenRepository.findByUserAndRevokedFalse(user).forEach(token -> {
            token.setRevoked(true);
            token.setLastUsedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
        });
    }

    private String generateRefreshTokenValue() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", ex);
        }
    }
}
