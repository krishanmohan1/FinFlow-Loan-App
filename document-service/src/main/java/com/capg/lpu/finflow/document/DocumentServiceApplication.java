package com.capg.lpu.finflow.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for the Document Service microservice.
 * Bootstraps the Spring Boot application and initializes document management configurations.
 */
@SpringBootApplication
@Slf4j
public class DocumentServiceApplication {

    /**
     * Application entry point for initializing the FinFlow Document microservice.
     *
     * @param args Command-line arguments passed during startup.
     */
    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
        log.info("Document Service Started on port 9093");
        log.info("Listening on RabbitMQ queue: loanQueue");
    }
}
