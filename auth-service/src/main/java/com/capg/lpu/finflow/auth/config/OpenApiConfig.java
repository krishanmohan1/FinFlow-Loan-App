package com.capg.lpu.finflow.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI (Swagger) documentation of the Auth Service APIs.
 * It provides custom metadata regarding the API's endpoints and versions.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI bean with service-specific details.
     *
     * @return an instance of OpenAPI populated with API metadata
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