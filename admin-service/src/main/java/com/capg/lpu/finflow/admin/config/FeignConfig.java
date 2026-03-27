package com.capg.lpu.finflow.admin.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign clients in the Admin Service.
 * Defines shared request interceptors for microservice-to-microservice communication.
 */
@Configuration
public class FeignConfig {

    /**
     * Creates a RequestInterceptor to inject administrative security headers into all outgoing Feign requests.
     * This ensures downstream services recognize the request as coming from an authorized administrator.
     *
     * @return A RequestInterceptor that adds 'X-Auth-Role' and 'X-Auth-Username' headers.
     */
    @Bean
    public RequestInterceptor adminHeaderInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Set the role to ADMIN to satisfy security requirements of target microservices.
                template.header("X-Auth-Role", "ADMIN");
                // Set the default admin username for auditing and tracking purposes.
                template.header("X-Auth-Username", "admin");
            }
        };
    }
}