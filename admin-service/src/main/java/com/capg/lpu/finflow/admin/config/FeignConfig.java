package com.capg.lpu.finflow.admin.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration orchestrating unified HTTP interception mechanisms ensuring securely structured contexts.
 */
@Configuration
public class FeignConfig {

    /**
     * Intercepts dynamically generated HTTP mappings resolving structural header dependencies specifically assuring downstream validations recognize administrative paths.
     *
     * @return configured RequestInterceptor deploying static headers constantly
     */
    @Bean
    public RequestInterceptor adminHeaderInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Defines authorization clearance requirements satisfying explicitly mapped downstream routing.
                template.header("X-Auth-Role", "ADMIN");
                // Attaches constant metadata implicitly facilitating internal system audit log verifications.
                template.header("X-Auth-Username", "admin");
            }
        };
    }
}