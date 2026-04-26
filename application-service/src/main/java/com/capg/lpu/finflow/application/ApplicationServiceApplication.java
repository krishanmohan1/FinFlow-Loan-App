package com.capg.lpu.finflow.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for the Application Service microservice.
 * Bootstraps the Spring Boot application context and initializes service-specific configurations.
 */
@SpringBootApplication
@Slf4j
public class ApplicationServiceApplication {

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
