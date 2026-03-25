package com.capg.lpu.finflow.admin.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    // ✅ Automatically injects ADMIN role headers into every outgoing Feign request
    // This is required because Application Service and Document Service
    // read X-Auth-Role and X-Auth-Username from request headers to authorize access
    @Bean
    public RequestInterceptor adminHeaderInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // ✅ Inject ADMIN role — required by downstream services
                template.header("X-Auth-Role", "ADMIN");
                // ✅ Inject a system admin username — used for audit trails
                template.header("X-Auth-Username", "admin");
            }
        };
    }
}