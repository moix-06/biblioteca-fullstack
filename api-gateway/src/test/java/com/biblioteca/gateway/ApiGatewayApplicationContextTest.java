package com.biblioteca.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.gateway.discovery.locator.enabled=false"
})
class ApiGatewayApplicationContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should be loaded");
    }

    @Test
    void loggingGlobalFilterBeanIsPresent() {
        boolean hasFilter = applicationContext.containsBean("loggingGlobalFilter");
        assertTrue(hasFilter, "loggingGlobalFilter bean should be registered");
    }

    @Test
    void corsWebFilterBeanIsPresent() {
        boolean hasFilter = applicationContext.containsBean("corsWebFilter");
        assertTrue(hasFilter, "corsWebFilter bean should be registered");
    }
}
