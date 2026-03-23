package com.finflow.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApplicationServiceApplication.class, args);
        log.info("✅ Application Service Started on port 9092");
    }
}