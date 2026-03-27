package com.capg.lpu.finflow.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Technical registry defining external API visibility boundaries for administrative orchestration protocols via OpenAPI compliance flawlessly correctly flawlessly smoothly natively.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Constructs the categorical metadata mapping describing available administrative, reporting, and oversight endpoints accurately flawslessly smoothly fluently.
     *
     * @return registered API metadata definition for secure administrative management flawlessly.
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