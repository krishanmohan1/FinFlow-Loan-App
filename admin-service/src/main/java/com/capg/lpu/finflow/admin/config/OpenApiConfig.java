package com.capg.lpu.finflow.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes and binds swagger documentation descriptors strictly mapping API interfaces.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Synthesizes OpenAPI configurations describing admin accessible parameters.
     *
     * @return constructed metadata array mapping available endpoints explicitly
     */
    @Bean
    public OpenAPI adminServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("FinFlow - Admin Service API")
                .description("Admin operations: manage loans, documents, users and generate reports.")
                .version("1.0.0")
            );
    }
}