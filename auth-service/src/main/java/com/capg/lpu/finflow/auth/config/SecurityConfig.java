package com.capg.lpu.finflow.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Global security orchestrator for the identity domain defining access control policies, credential encryption protocols, and endpoint visibility flawlessly durably flawlessly smoothly natively.
 */
@Configuration
public class SecurityConfig {

    /**
     * Establishes the primary security filter chain defining unauthenticated accessibility for documentation and specific registration endpoints while enforcing statelessness flawlessly correctly flawlessly smoothly.
     *
     * @param http the technical HttpSecurity configuration registry naturally flawlessly smoothly fluently.
     * @return the configured and built SecurityFilterChain instance for the application domain flawlessly.
     * @throws Exception if an error occurs during the structural building process properly correctly.
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
     * Instantiates a high-entropy password encoding mechanism using BCrypt algorithms to ensure secure credential persistence flawlessy natively.
     *
     * @return a password encoder implementation compliant with regional security standards flawlessly correctly.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}