package com.biblioteca.usuarios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ms.multas.url:http://localhost:8086}")
    private String multasUrl;

    @Bean
    public WebClient webClientMultas() {
        return WebClient.builder()
                .baseUrl(multasUrl + "/api/multas")
                .build();
    }
}