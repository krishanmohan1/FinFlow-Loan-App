package com.capg.lpu.finflow.gateway;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class ApiGatewayMainTest {

    @Test
    void mainBootstrapsApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ApiGatewayApplication.main(new String[0]);
            springApplication.verify(() -> SpringApplication.run(ApiGatewayApplication.class, new String[0]));
        }
    }
}
