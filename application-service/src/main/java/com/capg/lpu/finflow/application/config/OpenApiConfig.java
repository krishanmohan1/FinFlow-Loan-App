package com.capg.lpu.finflow.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

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