package com.biblioteca.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.cloud.netflix.eureka.server.EurekaServerBootstrap;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
class EurekaServerApplicationContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should be loaded");
    }

    @Test
    void eurekaServerBootstrapBeanIsPresent() {
        boolean hasBootstrap = applicationContext.getBeansOfType(EurekaServerBootstrap.class).size() > 0
                || applicationContext.containsBean("eurekaServerBootstrap");
        assertTrue(hasBootstrap, "EurekaServerBootstrap bean should be registered");
    }
}
