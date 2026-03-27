package com.capg.lpu.finflow.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 * Defines the documentation metadata for the Application microservice.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI instance with service-specific metadata.
     *
     * @return a configured OpenAPI instance for the application service documentation.
     */
    @Bean
    public OpenAPI applicationServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("FinFlow — Application Service API")
                .description("Loan applications: apply, view, update status, approve and reject.")
                .version("1.0.0")
            );
    }
}