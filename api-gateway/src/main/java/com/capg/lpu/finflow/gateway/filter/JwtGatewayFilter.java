package com.capg.lpu.finflow.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.capg.lpu.finflow.gateway.security.JwtUtil;

@Component
public class JwtGatewayFilter extends AbstractGatewayFilterFactory<Object> {

    private static final Logger log = LoggerFactory.getLogger(JwtGatewayFilter.class);

    private final JwtUtil jwtUtil;

    public JwtGatewayFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            log.info(" Incoming request → [{} {}]", method, path);

            //  PUBLIC ENDPOINTS — skip JWT check
            if (isPublicPath(path)) {
                log.info(" Public path, skipping JWT check → {}", path);
                return chain.filter(exchange);
            }

            //  GET AUTH HEADER
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn(" Missing or invalid Authorization header for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            //  VALIDATE TOKEN
            if (!jwtUtil.validateToken(token)) {
                log.warn(" Invalid or expired token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            log.info(" Token valid → user: {}, role: {}, path: {}", username, role, path);

            //  ADMIN SERVICE — ADMIN only
            if (path.startsWith("/admin") && !"ADMIN".equals(role)) {
                log.warn(" Access denied → user: {} (role: {}) tried to access ADMIN path: {}",
                        username, role, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            //  APPLICATION SERVICE — USER + ADMIN
            if (path.startsWith("/application") &&
                    !("USER".equals(role) || "ADMIN".equals(role))) {
                log.warn(" Access denied → user: {} (role: {}) tried to access APPLICATION path: {}",
                        username, role, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            //  DOCUMENT SERVICE — USER + ADMIN
            if (path.startsWith("/document") &&
                    !("USER".equals(role) || "ADMIN".equals(role))) {
                log.warn(" Access denied → user: {} tried to access DOCUMENT path: {}", username, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            //  PASS USER INFO TO DOWNSTREAM SERVICES via headers
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-Auth-Username", username)
                    .header("X-Auth-Role", role)
                    .build();

            log.info(" Forwarding request to service with headers X-Auth-Username={}, X-Auth-Role={}",
                    username, role);

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    //  Public paths that skip JWT
    private boolean isPublicPath(String path) {
        return path.contains("/auth/login")
                || path.contains("/auth/register")
                || path.contains("/actuator");
    }
}