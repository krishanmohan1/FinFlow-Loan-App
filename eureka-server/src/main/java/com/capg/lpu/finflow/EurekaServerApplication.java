package com.capg.lpu.finflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main entry point for the Eureka Service Registry.
 * Required for microservices to discover and communicate with each other dynamically.
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

    private static final Logger log = LoggerFactory.getLogger(EurekaServerApplication.class);

    /**
     * Bootstraps the Eureka Server Spring Boot application.
     *
     * @param args runtime command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        log.info("Eureka Server Started on port 8761");
        log.info("Dashboard URL - http://localhost:8761");
    }
}