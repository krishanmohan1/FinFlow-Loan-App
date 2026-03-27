package com.capg.lpu.finflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test to ensure the Eureka Server context loads correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context starts without issues.
    }

}
