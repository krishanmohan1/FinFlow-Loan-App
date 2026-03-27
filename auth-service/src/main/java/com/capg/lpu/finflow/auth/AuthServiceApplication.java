package com.capg.lpu.finflow.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Auth Service.
 * Bootstraps the identity and access management microservice, initializing the application context.
 */
@SpringBootApplication
public class AuthServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);

    /**
     * Starts the Auth Service microservice and initializes secure authentication workflows.
     *
     * @param args Command-line arguments passed during application startup.
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        log.info("Auth Service Started on port 9091");
    }
}