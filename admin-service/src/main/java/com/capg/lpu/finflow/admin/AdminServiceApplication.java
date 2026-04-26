package com.capg.lpu.finflow.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for the Admin Service microservice.
 * This service acts as an orchestrator for administrative tasks, using Feign clients
 * to communicate with other microservices. Database auto-configuration is excluded
 * as this service does not maintain its own persistent storage.
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
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
