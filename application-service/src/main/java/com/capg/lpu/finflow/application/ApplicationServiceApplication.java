package com.capg.lpu.finflow.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Application Service microservice.
 * Bootstraps the Spring Boot application context and initializes service-specific configurations.
 */
@SpringBootApplication
public class ApplicationServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServiceApplication.class);

    /**
     * Application entry point for initializing the FinFlow Application microservice.
     *
     * @param args Command-line arguments passed during startup.
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationServiceApplication.class, args);
        log.info("Application Service Started on port 9092");
    }
}