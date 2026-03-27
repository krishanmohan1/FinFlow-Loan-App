package com.capg.lpu.finflow.document.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 * Defines the documentation metadata for the Document microservice.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI instance with service-specific metadata.
     *
     * @return a configured OpenAPI instance for the document service documentation.
     */
    @Bean
    public OpenAPI documentServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("FinFlow — Document Service API")
                .description("Upload, update, verify and delete loan documents.")
                .version("1.0.0")
            );
    }
}