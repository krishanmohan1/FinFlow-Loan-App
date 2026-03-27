package com.capg.lpu.finflow.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the Admin Service.
 * Acts as a centralized orchestration layer for administrative tasks.
 * Excludes database auto-configuration as this service relies entirely 
 * on Feign clients interacting with other microservices.
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@EnableFeignClients
public class AdminServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceApplication.class);

    /**
     * Bootstraps the Admin Service context.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
        log.info("Admin Service Started on port 9094");
    }
}