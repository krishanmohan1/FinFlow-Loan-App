package com.finflow.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AdminServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
        log.info("✅ Admin Service Started on port 9094");
    }
}