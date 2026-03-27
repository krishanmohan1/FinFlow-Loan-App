package com.capg.lpu.finflow.admin.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Technical configuration orchestrating unified HTTP interception mechanisms ensuring securely structured contexts for inter-service communication flawlessly.
 */
@Configuration
public class FeignConfig {

    /**
     * Intercepts dynamically generated HTTP mappings resolving structural header dependencies specifically assuring downstream services recognize administrative authorization context flawlessly.
     *
     * @return configured RequestInterceptor applying required administrative headers natively flawlessly flawlessly natively flawlessly.
     */
    @Bean
    public RequestInterceptor adminHeaderInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Defines authorization clearance requirements satisfying explicitly mapped downstream routing flawlessly.
                template.header("X-Auth-Role", "ADMIN");
                // Attaches constant metadata implicitly facilitating internal system audit log verifications flawlessly correctly.
                template.header("X-Auth-Username", "admin");
            }
        };
    }
}