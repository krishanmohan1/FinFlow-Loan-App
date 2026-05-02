package com.capg.lpu.finflow.auth;

import com.capg.lpu.finflow.auth.config.DataInitializer;
import com.capg.lpu.finflow.auth.config.OpenApiConfig;
import com.capg.lpu.finflow.auth.config.SecurityConfig;
import com.capg.lpu.finflow.auth.entity.RefreshToken;
import com.capg.lpu.finflow.auth.entity.User;
import com.capg.lpu.finflow.auth.repository.UserRepository;
import com.capg.lpu.finflow.auth.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthSupportTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void jwtUtilGeneratesAndValidatesTokens() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtUtil, "issuer", "finflow-auth");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 60000L);

        String token = jwtUtil.generateToken("user1", "USER");

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("user1");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("USER");
        assertThat(jwtUtil.validateToken(token, "user1")).isTrue();
        assertThat(jwtUtil.validateToken(token, "other")).isFalse();

        ReflectionTestUtils.setField(jwtUtil, "issuer", "different-issuer");
        assertThat(jwtUtil.validateToken(token, "user1")).isFalse();
        assertThat(jwtUtil.getAccessTokenExpirationMs()).isEqualTo(60000L);
    }

    @Test
    void openApiAndPasswordEncoderBeansAreCreated() {
        OpenAPI openAPI = new OpenApiConfig().authServiceOpenAPI();
        PasswordEncoder encoder = new SecurityConfig().passwordEncoder();

        assertThat(openAPI.getInfo().getTitle()).contains("Auth Service");
        assertThat(encoder.matches("secret", encoder.encode("secret"))).isTrue();
    }

    @Test
    void dataInitializerCreatesAdminWhenMissing() throws Exception {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        new DataInitializer(userRepository, passwordEncoder).run();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void dataInitializerSkipsCreationWhenAdminExists() throws Exception {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        new DataInitializer(userRepository, passwordEncoder).run();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void refreshTokenPrePersistInitializesCreatedAt() {
        RefreshToken token = RefreshToken.builder().tokenHash("hash").expiresAt(LocalDateTime.now().plusDays(1)).build();

        token.prePersist();

        assertThat(token.getCreatedAt()).isNotNull();
    }

    @Test
    void mainBootstrapsApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            AuthServiceApplication.main(new String[0]);
            springApplication.verify(() -> SpringApplication.run(AuthServiceApplication.class, new String[0]));
        }
    }
}
