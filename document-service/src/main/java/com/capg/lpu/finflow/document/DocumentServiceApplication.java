package com.capg.lpu.finflow.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Document Service microservice.
 * Bootstraps the Spring Boot application and initializes document management configurations.
 */
@SpringBootApplication
public class DocumentServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceApplication.class);

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