package com.capg.lpu.finflow.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for the Auth Service.
 * Bootstraps the identity and access management microservice, initializing the application context.
 */
@SpringBootApplication
@Slf4j
public class AuthServiceApplication {

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
