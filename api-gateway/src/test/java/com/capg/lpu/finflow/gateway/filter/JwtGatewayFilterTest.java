package com.capg.lpu.finflow.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import com.capg.lpu.finflow.gateway.security.JwtUtil;

import reactor.core.publisher.Mono;

/**
 * Unit tests for gateway JWT filtering behavior.
 */
class JwtGatewayFilterTest {

    private JwtUtil jwtUtil;
    private JwtGatewayFilter filter;
    private GatewayFilterChain chain;
    private ServerWebExchange[] forwardedExchange;

    @BeforeEach
    void setUp() {
        jwtUtil = org.mockito.Mockito.mock(JwtUtil.class);
        filter = new JwtGatewayFilter(jwtUtil);
        forwardedExchange = new ServerWebExchange[1];
        chain = exchange -> {
            forwardedExchange[0] = exchange;
            return Mono.empty();
        };
    }

    @Test
    void apply_allowsPublicPathWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/register").build());

        filter.apply(new Object()).filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void apply_rejectsProtectedPathWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/application/all").build());

        filter.apply(new Object()).filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void apply_rejectsAdminPathForNonAdminRole() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/admin/reports")
                        .header("Authorization", "Bearer test-token")
                        .build());

        when(jwtUtil.validateToken("test-token")).thenReturn(true);
        when(jwtUtil.extractUsername("test-token")).thenReturn("user1");
        when(jwtUtil.extractRole("test-token")).thenReturn("USER");

        filter.apply(new Object()).filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void apply_allowsCurrentUserProfilePathForBorrower() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/users/me")
                        .header("Authorization", "Bearer test-token")
                        .build());

        when(jwtUtil.validateToken("test-token")).thenReturn(true);
        when(jwtUtil.extractUsername("test-token")).thenReturn("user1");
        when(jwtUtil.extractRole("test-token")).thenReturn("USER");

        filter.apply(new Object()).filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isNull();
        ServerHttpRequest request = forwardedExchange[0].getRequest();
        assertThat(request.getHeaders().getFirst("X-Auth-Username")).isEqualTo("user1");
        assertThat(request.getHeaders().getFirst("X-Auth-Role")).isEqualTo("USER");
    }

    @Test
    void apply_rejectsAuthAdminPathForBorrower() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/auth/admin/register")
                        .header("Authorization", "Bearer test-token")
                        .build());

        when(jwtUtil.validateToken("test-token")).thenReturn(true);
        when(jwtUtil.extractUsername("test-token")).thenReturn("user1");
        when(jwtUtil.extractRole("test-token")).thenReturn("USER");

        filter.apply(new Object()).filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
