package com.capg.lpu.finflow.auth;

import com.capg.lpu.finflow.auth.dto.AuthResponse;
import com.capg.lpu.finflow.auth.dto.LoginRequest;
import com.capg.lpu.finflow.auth.dto.RegisterRequest;
import com.capg.lpu.finflow.auth.dto.UserResponse;
import com.capg.lpu.finflow.auth.entity.User;
import com.capg.lpu.finflow.auth.repository.UserRepository;
import com.capg.lpu.finflow.auth.security.JwtUtil;
import com.capg.lpu.finflow.auth.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for AuthService ensuring robust operations across registrations, logins, and profile management.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    /**
     * Initializes a sample user profile before initiating explicit tests.
     */
    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    /**
     * Validates logical flow persisting successful registrations mapped into active tokens reliably.
     */
    @Test
    @DisplayName("register - success: new user registered and token returned")
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo("USER");
        assertThat(response.getMessage()).isEqualTo("Registration successful");

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken("testuser", "USER");
    }

    /**
     * Enforces the systematic prevention of duplicate profiles.
     */
    @Test
    @DisplayName("register - fail: username already taken")
    void register_usernameAlreadyTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    /**
     * Explicit authentication logic test expecting successful JWT responses for matched hashes.
     */
    @Test
    @DisplayName("login - success: valid credentials return token")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getMessage()).isEqualTo("Login successful");

        verify(jwtUtil).generateToken("testuser", "USER");
    }

    /**
     * Safeguards missing account parsing flows against implicit null pointers correctly issuing runtime failures.
     */
    @Test
    @DisplayName("login - fail: user not found")
    void login_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("pass");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(jwtUtil, never()).generateToken(any(), any());
    }

    /**
     * Strictly verifies access blockage of explicit disabled identities.
     */
    @Test
    @DisplayName("login - fail: account is inactive")
    void login_inactiveAccount() {
        sampleUser.setActive(false);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("inactive");
    }

    /**
     * Prevents false positives allowing compromised password matching securely rejecting hashes failing evaluation.
     */
    @Test
    @DisplayName("login - fail: wrong password")
    void login_wrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongpass", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid credentials");
    }

    /**
     * Asserts proper unpaginated array building isolating data specifically. 
     */
    @Test
    @DisplayName("getAllUsers - returns list of UserResponse")
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserResponse> result = authService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        assertThat(result.get(0).getRole()).isEqualTo("USER");
    }

    /**
     * Triggers distinct retrieval validations isolating specified sequences reliably mapping internal values.
     */
    @Test
    @DisplayName("getUserById - success: returns correct user")
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponse result = authService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    /**
     * Verifies system resistance handling misassigned identities lacking relational counterparts securely intercepting runtime defaults.
     */
    @Test
    @DisplayName("getUserById - fail: user not found")
    void getUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getUserById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");
    }

    /**
     * Validates proper state saving applying multi-variable attribute modifications seamlessly.
     */
    @Test
    @DisplayName("updateUser - success: role and active status updated")
    void updateUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse result = authService.updateUser(1L, "ADMIN", false);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    /**
     * Assures strict boundaries blocking faulty hierarchy escalations outside constrained USER and ADMIN boundaries.
     */
    @Test
    @DisplayName("updateUser - fail: invalid role value")
    void updateUser_invalidRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> authService.updateUser(1L, "SUPERUSER", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role must be USER or ADMIN");
    }

    /**
     * Affirms the complete disable sequence enforcing functional locking without complete profile deletion explicitly.
     */
    @Test
    @DisplayName("deactivateUser - success: user is deactivated")
    void deactivateUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse result = authService.deactivateUser(1L);

        assertThat(result.isActive()).isFalse();
        verify(userRepository).save(any(User.class));
    }

    /**
     * Demonstrates that attempting profile disable operations against non-existent identities correctly halts execution.
     */
    @Test
    @DisplayName("deactivateUser - fail: user not found")
    void deactivateUser_notFound() {
        when(userRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.deactivateUser(55L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 55");
    }
}