package com.biblioteca.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorsConfigTest {

    @Test
    void corsWebFilter_noEsNull() {
        CorsConfig config = new CorsConfig();
        CorsWebFilter filter = config.corsWebFilter();
        assertNotNull(filter, "CorsWebFilter bean should not be null");
    }

    @Test
    void corsWebFilter_permiteMetodosEsperados() {
        CorsConfig config = new CorsConfig();
        List<String> expectedMethods = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
        CorsWebFilter filter = config.corsWebFilter();
        assertNotNull(filter);
        assertTrue(expectedMethods.contains("GET"));
        assertTrue(expectedMethods.contains("POST"));
        assertTrue(expectedMethods.contains("OPTIONS"));
        assertEquals(6, expectedMethods.size());
    }
}
