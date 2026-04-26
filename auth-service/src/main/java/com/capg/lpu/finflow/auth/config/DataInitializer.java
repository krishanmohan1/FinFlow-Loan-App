package com.capg.lpu.finflow.auth.config;

import com.capg.lpu.finflow.auth.entity.User;
import com.capg.lpu.finflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

/**
 * Global component to initialize foundational system data upon application startup.
 * Specifically handles the creation of a default administrator account if none exists.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Executes logic after application context is fully loaded.
     * Checks for the presence of an 'admin' user and creates it with administrative privileges if absent.
     *
     * @param args Command-line arguments.
     */
    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            log.info("System Initialization: Admin account not found. Creating default admin...");
            
            User admin = User.builder()
                    .username("admin")
                    .fullName("FinFlow Administrator")
                    .email("admin@finflow.example")
                    .phoneNumber("9876543210")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .addressLine1("FinFlow HQ, Business District")
                    .city("Bengaluru")
                    .state("Karnataka")
                    .postalCode("560001")
                    .occupation("Operations Admin")
                    .annualIncome(1200000.0)
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            log.info("System Initialization: Default admin created (username: admin, password: admin123)");
        } else {
            log.info("System Check: Administrative accounts verified. No initialization required.");
        }
    }
}
