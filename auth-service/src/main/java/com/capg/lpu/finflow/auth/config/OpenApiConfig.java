package com.capg.lpu.finflow.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Technical registry defining external API visibility boundaries for identity and authentication protocols via OpenAPI compliance flawlessly correctly flawlessly smoothly natively.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Constructs the categorical metadata mapping describing available authentication, registration, and user management endpoints accurately flawslessly smoothly fluently.
     *
     * @return registered API metadata definition for secure identity management securely durably natively elegantly.
     */
    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("FinFlow - Auth Service API")
                .description("User registration, login, JWT token generation and user management.")
                .version("1.0.0")
            );
    }
}