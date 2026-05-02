package com.capg.lpu.finflow.auth.controller;

import com.capg.lpu.finflow.auth.dto.AuthResponse;
import com.capg.lpu.finflow.auth.dto.AuthenticatedSession;
import com.capg.lpu.finflow.auth.dto.LoginRequest;
import com.capg.lpu.finflow.auth.dto.ProfileUpdateRequest;
import com.capg.lpu.finflow.auth.dto.RegisterRequest;
import com.capg.lpu.finflow.auth.dto.UserResponse;
import com.capg.lpu.finflow.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private AuthenticatedSession session;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authController, "refreshCookieName", "finflow_refresh");
        ReflectionTestUtils.setField(authController, "refreshExpirationMs", 60000L);
        ReflectionTestUtils.setField(authController, "refreshCookieSecure", false);

        session = new AuthenticatedSession(
                new AuthResponse("token", "user1", "USER", "ok", 900000L),
                "refresh-token-value"
        );
        userResponse = UserResponse.builder()
                .id(1L)
                .username("user1")
                .fullName("User One")
                .email("user1@example.com")
                .phoneNumber("9876543210")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .addressLine1("Demo address line 1")
                .city("Bengaluru")
                .state("Karnataka")
                .postalCode("560001")
                .occupation("Engineer")
                .annualIncome(900000.0)
                .role("USER")
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    @Test
    void healthEndpointsReturnExpectedMessages() {
        assertThat(authController.test().getBody()).isEqualTo("Auth Service is Running");
        assertThat(authController.adminTest().getBody()).isEqualTo("Admin Access Granted");
        assertThat(authController.userTest().getBody()).isEqualTo("User Access Granted");
    }

    @Test
    void registerAttachesRefreshCookie() {
        RegisterRequest request = new RegisterRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(authService.register(request)).thenReturn(session);

        AuthResponse body = authController.register(request, response).getBody();

        assertThat(body).isEqualTo(session.response());
        assertThat(response.getHeader("Set-Cookie")).contains("finflow_refresh=refresh-token-value");
    }

    @Test
    void loginAttachesRefreshCookie() {
        LoginRequest request = new LoginRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(authService.login(request)).thenReturn(session);

        AuthResponse body = authController.login(request, response).getBody();

        assertThat(body).isEqualTo(session.response());
        assertThat(response.getHeader("Set-Cookie")).contains("finflow_refresh=refresh-token-value");
    }

    @Test
    void refreshReadsCookieAndRotatesSession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("finflow_refresh", "old-refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(authService.refreshSession("old-refresh-token")).thenReturn(session);

        AuthResponse body = authController.refresh(request, response).getBody();

        assertThat(body).isEqualTo(session.response());
        assertThat(response.getHeader("Set-Cookie")).contains("finflow_refresh=refresh-token-value");
    }

    @Test
    void logoutRevokesSessionAndClearsCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("finflow_refresh", "logout-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        String body = authController.logout(request, response).getBody();

        assertThat(body).isEqualTo("Logout successful");
        assertThat(response.getHeader("Set-Cookie")).contains("finflow_refresh=");
        verify(authService).logout("logout-token");
    }

    @Test
    void userEndpointsDelegateToService() {
        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        AuthController.UpdateUserRequest adminUpdate = new AuthController.UpdateUserRequest();
        adminUpdate.setRole("ADMIN");
        adminUpdate.setActive(Boolean.TRUE);

        when(authService.registerAdmin(new RegisterRequest())).thenReturn(userResponse);
        when(authService.getAllUsers()).thenReturn(List.of(userResponse));
        when(authService.getUserById(1L)).thenReturn(userResponse);
        when(authService.getCurrentUser("user1")).thenReturn(userResponse);
        when(authService.updateCurrentUser("user1", updateRequest)).thenReturn(userResponse);
        when(authService.updateUser(1L, "ADMIN", true)).thenReturn(userResponse);
        when(authService.deactivateUser(1L)).thenReturn(userResponse);

        assertThat(authController.getAllUsers().getBody()).containsExactly(userResponse);
        assertThat(authController.getUserById(1L).getBody()).isEqualTo(userResponse);
        assertThat(authController.getCurrentUser("user1").getBody()).isEqualTo(userResponse);
        assertThat(authController.updateCurrentUser("user1", updateRequest).getBody()).isEqualTo(userResponse);
        assertThat(authController.updateUser(1L, adminUpdate).getBody()).isEqualTo(userResponse);
        assertThat(authController.deactivateUser(1L).getBody()).isEqualTo(userResponse);
    }
}
