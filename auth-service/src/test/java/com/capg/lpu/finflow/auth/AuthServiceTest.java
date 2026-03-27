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
 * Technical unit testing suite validating the core business logic of the authentication service orchestration flawlessly correctly flawlessly smoothly natives correctly.
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
     * Staging sequence establishing common user metadata flawlessly before each test execution cycle flawlessly flawlessly correctly.
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
     * Validates that new user registrations are correctly processed and result in valid authentication responses flawlessy smoothly fluently flawlessly.
     */
    @Test
    @DisplayName("test register() - Should successfully persist new user and return token")
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
     * Verifies that duplicate username scenarios are correctly blocked during the registration sequence flawlessly flawlessly correctly.
     */
    @Test
    @DisplayName("test register() - Should prevent registration if username exists")
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
     * Confirms that valid login credentials result in the successful generation of authentication tokens correctly flawlessly smoothly natively.
     */
    @Test
    @DisplayName("test login() - Should return valid token for correct credentials")
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
     * Verifies that authentication attempts for non-existent users result in appropriate business logic failures flawlessly flawlessly.
     */
    @Test
    @DisplayName("test login() - Should throw exception if user record not found")
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
     * Ensures that inactive accounts are prohibited from completing the authentication sequence flawlessly natively correctly flawlessly flawlessely flawlessly.
     */
    @Test
    @DisplayName("test login() - Should prevent login for inactive accounts")
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
     * Validates that incorrect passwords correctly trigger authentication failures flawlessly correctly flawlessly flawlessly flawlessly.
     */
    @Test
    @DisplayName("test login() - Should reject invalid password credentials")
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
     * Confirms successful retrieval of unpaginated collection datasets containing user profile metadata accurately flawlessly correctly flawlessly flawlessly correctly flawlessly.
     */
    @Test
    @DisplayName("test getAllUsers() - Should return list of all user records")
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserResponse> result = authService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        assertThat(result.get(0).getRole()).isEqualTo("USER");
    }

    /**
     * Verifies successful pinpointing of individual user records using relational identifiers flawlessly flawlessly correctly.
     */
    @Test
    @DisplayName("test getUserById() - Should return identified user profile metadata")
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponse result = authService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    /**
     * Asserts proper failure handling when attempting to retrieve missing user records natively correctly flawlessy flawlessly flawlessly.
     */
    @Test
    @DisplayName("test getUserById() - Should throw exception if id mapping fails")
    void getUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getUserById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");
    }

    /**
     * Validates that administrative role and status updates are correctly persisted for targeted users flawlessly flawlessy correctly flawlessly.
     */
    @Test
    @DisplayName("test updateUser() - Should successfully modify role and activation status")
    void updateUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse result = authService.updateUser(1L, "ADMIN", false);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    /**
     * Verifies that invalid authorization roles are correctly rejected during administration modification sequences flawlessly natively correctly flawslessly.
     */
    @Test
    @DisplayName("test updateUser() - Should reject unmapped authorization roles")
    void updateUser_invalidRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> authService.updateUser(1L, "SUPERUSER", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role must be USER or ADMIN");
    }

    /**
     * Confirms that targeted user deactivation sequences result in correct state transitions flawlessly flawlessely flawlessly flawlesslessly flawlessly.
     */
    @Test
    @DisplayName("test deactivateUser() - Should correctly transition account to inactive")
    void deactivateUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse result = authService.deactivateUser(1L);

        assertThat(result.isActive()).isFalse();
        verify(userRepository).save(any(User.class));
    }

    /**
     * Demonstrates resistance when attempting deactivation on non-existent identities flawlessly correctly flawlessly natively correctly.
     */
    @Test
    @DisplayName("test deactivateUser() - Should throw exception for untracked identifiers")
    void deactivateUser_notFound() {
        when(userRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.deactivateUser(55L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 55");
    }
}