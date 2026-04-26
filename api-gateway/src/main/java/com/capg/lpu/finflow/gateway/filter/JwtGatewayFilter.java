package com.capg.lpu.finflow.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.capg.lpu.finflow.gateway.security.JwtUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Filter that intercepts incoming requests to the API Gateway.
 * It validates JWT tokens for protected endpoints and manages 
 * role-based access control checking before forwarding to downstream services.
 */
@Component
@Slf4j
public class JwtGatewayFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    /**
     * Constructs a new JwtGatewayFilter with the necessary dependency.
     *
     * @param jwtUtil the utility class responsible for extracting and validating JWTs
     */
    public JwtGatewayFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Applies the filter logic to the incoming request. Check for public paths,
     * extract and validate tokens, applies role checks, and forwards headers.
     *
     * @param config the configuration object for this filter instance
     * @return the GatewayFilter implementation
     */
    
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            log.info("Incoming request - [{} {}]", method, path);

            // Skip JWT validation for public endpoints
            if (isPublicPath(path)) {
                log.info("Public path, skipping JWT check - {}", path);
                return chain.filter(exchange);
            }

            // Extract the Authorization header
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            // Validate the extracted JWT token
            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid or expired token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            if (!"USER".equals(role) && !"ADMIN".equals(role)) {
                log.warn("Access denied - invalid role {} for path {}", role, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            log.info("Token valid - user: {}, role: {}, path: {}", username, role, path);

            // Role-based Access Control (RBAC): Ensure only ADMIN role accesses /admin paths
            if (path.startsWith("/admin") && !"ADMIN".equals(role)) {
                log.warn("Access denied - user: {} (role: {}) tried to access ADMIN path: {}",
                        username, role, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Role-based Access Control: Ensure only ADMIN role accesses sensitive user management paths in Auth Service
            if (path.startsWith("/auth/users") && !"ADMIN".equals(role)) {
                log.warn("Access denied - user: {} (role: {}) tried to access sensitive AUTH/USERS path: {}",
                        username, role, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Role-based Access Control: Verify users have proper roles for /application paths
            if (path.startsWith("/application") &&
                    !("USER".equals(role) || "ADMIN".equals(role))) {
                log.warn("Access denied - user: {} (role: {}) tried to access APPLICATION path: {}",
                        username, role, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Role-based Access Control: Verify users have proper roles for /document paths
            if (path.startsWith("/document") &&
                    !("USER".equals(role) || "ADMIN".equals(role))) {
                log.warn("Access denied - user: {} tried to access DOCUMENT path: {}", username, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Pass the extracted user information to downstream services via HTTP headers
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-Auth-Username", username)
                    .header("X-Auth-Role", role)
                    .build();

            log.info("Forwarding request to service with headers X-Auth-Username={}, X-Auth-Role={}",
                    username, role);

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    /**
     * Determines whether the given path corresponds to a public, unsecured endpoint.
     *
     * @param path the request path to evaluate
     * @return true if the path is public, otherwise false
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/register")
                || path.startsWith("/auth/test")
                || path.contains("/actuator")
                || path.contains("/v3/api-docs")
                || path.contains("swagger-ui");
    }
}
