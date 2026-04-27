package com.capg.lpu.finflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.capg.lpu.finflow.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .email("testuser@example.com")
                .phoneNumber("9876543210")
                .dateOfBirth(LocalDate.of(1995, 5, 10))
                .addressLine1("221B Baker Street, Demo Layout")
                .city("Bengaluru")
                .state("Karnataka")
                .postalCode("560001")
                .occupation("Software Engineer")
                .annualIncome(900000.0)
                .password("encodedPassword")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    @Test
    @DisplayName("register should persist new borrower and return token")
    void register_success() {
        RegisterRequest request = validRegisterRequest();

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn("mock.jwt.token");
        when(jwtUtil.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer((invocation) -> invocation.getArgument(0));

        AuthenticatedSession session = authService.register(request);
        AuthResponse response = session.response();

        assertThat(response.getAccessToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo("USER");
        assertThat(session.refreshToken()).isNotBlank();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("registerAdmin should persist a new admin account")
    void registerAdmin_success() {
        RegisterRequest request = validRegisterRequest();
        sampleUser.setRole("ADMIN");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse response = authService.registerAdmin(request);

        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(response.getUsername()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register should reject duplicate username")
    void register_duplicateUsername() {
        RegisterRequest request = validRegisterRequest();
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("register should reject duplicate email")
    void register_duplicateEmail() {
        RegisterRequest request = validRegisterRequest();
        request.setUsername("anotheruser");

        when(userRepository.existsByUsername("anotheruser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("login should return token for valid credentials")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn("mock.jwt.token");
        when(jwtUtil.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer((invocation) -> invocation.getArgument(0));

        AuthenticatedSession session = authService.login(request);
        AuthResponse response = session.response();

        assertThat(response.getMessage()).isEqualTo("Login successful");
        assertThat(response.getAccessToken()).isEqualTo("mock.jwt.token");
        assertThat(session.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("getCurrentUser should return borrower profile")
    void getCurrentUser_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        UserResponse response = authService.getCurrentUser("testuser");

        assertThat(response.getFullName()).isEqualTo("Test User");
        assertThat(response.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @DisplayName("updateCurrentUser should persist borrower profile changes")
    void updateCurrentUser_success() {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFullName("Updated User");
        request.setEmail("updated@example.com");
        request.setPhoneNumber("9876543211");
        request.setDateOfBirth(LocalDate.of(1994, 4, 9));
        request.setAddressLine1("Flat 12, Example Residency Block A");
        request.setCity("Mysuru");
        request.setState("Karnataka");
        request.setPostalCode("570001");
        request.setOccupation("Analyst");
        request.setAnnualIncome(650000.0);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer((invocation) -> invocation.getArgument(0));

        UserResponse response = authService.updateCurrentUser("testuser", request);

        assertThat(response.getFullName()).isEqualTo("Updated User");
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("getAllUsers should return mapped profiles")
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserResponse> result = authService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("updateUser should modify admin-controlled fields")
    void updateUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse result = authService.updateUser(1L, "ADMIN", false);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    private RegisterRequest validRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setFullName("Test User");
        request.setEmail("testuser@example.com");
        request.setPhoneNumber("9876543210");
        request.setDateOfBirth(LocalDate.of(1995, 5, 10));
        request.setAddressLine1("221B Baker Street, Demo Layout");
        request.setCity("Bengaluru");
        request.setState("Karnataka");
        request.setPostalCode("560001");
        request.setOccupation("Software Engineer");
        request.setAnnualIncome(900000.0);
        request.setPassword("password123");
        return request;
    }
}
