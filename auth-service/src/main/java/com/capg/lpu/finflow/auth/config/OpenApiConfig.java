package com.capg.lpu.finflow.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI/Swagger documentation in the Auth Service.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI instance with service-specific metadata for the authentication API.
     *
     * @return A configured OpenAPI instance for documentation purposes.
     */
    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("FinFlow - Auth Service API")
                .description("Authentication API for user registration, login, and JWT token management.")
                .version("1.0.0")
            );
    }
}