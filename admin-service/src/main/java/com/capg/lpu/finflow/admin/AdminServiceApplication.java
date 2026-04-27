package com.capg.lpu.finflow.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for the Admin Service microservice.
 * This service acts as an orchestrator for administrative tasks, uses Feign clients
 * to communicate with other microservices, and now owns its own audit/reporting database.
 */
@SpringBootApplication
@EnableFeignClients
@Slf4j
public class AdminServiceApplication {

    /**
     * Bootstraps the Admin Service application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
        log.info("Admin Service Started on port 9094");
    }
}
