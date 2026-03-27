package com.capg.lpu.finflow.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Global security configuration for the Auth Service.
 * Specifies the rules for web security, encryption, and endpoint accessibility.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures the security filter chain to allow unauthenticated access to documentation
     * and specific endpoints, effectively disabling CSRF as this service relies on stateless JWTs.
     *
     * @param http the HttpSecurity configuration object
     * @return the configured SecurityFilterChain object
     * @throws Exception if an error occurs while building the security configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }

    /**
     * Provides a BCrypt password encoder for secure password hashing.
     *
     * @return a password encoder implementation
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}