package com.capg.lpu.finflow;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class EurekaServerMainTest {

    @Test
    void mainBootstrapsApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            EurekaServerApplication.main(new String[0]);
            springApplication.verify(() -> SpringApplication.run(EurekaServerApplication.class, new String[0]));
        }
    }
}
