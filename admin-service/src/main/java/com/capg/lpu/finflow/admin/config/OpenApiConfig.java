package com.capg.lpu.finflow.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI/Swagger documentation in the Admin Service.
 * Defines the metadata and branding for the administrative API documentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI bean with general information about the Admin Service API.
     *
     * @return A configured OpenAPI instance for generating documentation.
     */
    @Bean
    public OpenAPI adminServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("FinFlow - Admin Service API")
                .description("Administrative API for managing loans, documents, users, and generating reports.")
                .version("1.0.0")
            );
    }
}