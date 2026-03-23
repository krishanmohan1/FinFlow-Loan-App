package com.finflow.gateway.filter;

import com.finflow.gateway.security.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtGatewayFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    public JwtGatewayFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // 🔓 PUBLIC ENDPOINTS
            if (path.contains("/auth/login") || path.contains("/auth/register")) {
                return chain.filter(exchange);
            }

            // 🔒 GET AUTH HEADER
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {
                // 🔍 EXTRACT DATA
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                // 🔐 VALIDATE TOKEN
                if (!jwtUtil.validateToken(token, username)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                // 🔥 ADMIN SERVICE SECURITY
                if (path.contains("/admin") && !"ADMIN".equals(role)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                // 🔥 APPLICATION SERVICE SECURITY (USER + ADMIN)
                if (path.contains("/application") &&
                        !("USER".equals(role) || "ADMIN".equals(role))) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // ✅ PASS REQUEST
            return chain.filter(exchange);
        };
    }
}