package com.capg.lpu.finflow.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the API Gateway service.
 * This service acts as the entry point for all incoming requests and routes them to the appropriate microservices.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    private static final Logger log = LoggerFactory.getLogger(ApiGatewayApplication.class);

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