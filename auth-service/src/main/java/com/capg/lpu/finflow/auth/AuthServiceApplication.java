package com.capg.lpu.finflow.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Authentication Service.
 * Manages user identities, secure registration, and generates JWT tokens.
 */
@SpringBootApplication
public class AuthServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);

    /**
     * Initializes the Authentication Spring Boot context.
     *
     * @param args runtime command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        log.info("Auth Service Started on port 9091");
    }
}