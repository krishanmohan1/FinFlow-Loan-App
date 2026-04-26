package com.capg.lpu.finflow.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * Main application class for the API Gateway service.
 * This service acts as the entry point for all incoming requests and routes them to the appropriate microservices.
 */
@SpringBootApplication
@Slf4j
public class ApiGatewayApplication {

    /**
     * The main method that bootstraps the API Gateway Spring Boot application.
     *
     * @param args command-line arguments passed during application startup
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        log.info("API Gateway Started on port 9090");
        log.info("All requests routed through -> http://localhost:9090");
    }
}
