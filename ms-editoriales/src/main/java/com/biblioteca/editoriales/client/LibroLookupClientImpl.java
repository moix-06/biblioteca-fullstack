package com.biblioteca.editoriales.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class LibroLookupClientImpl implements LibroLookupClient {

    private final WebClient webClientLibros;

    public LibroLookupClientImpl(@Value("${ms.libros.url}") String librosUrl) {
        this.webClientLibros = WebClient.builder().baseUrl(librosUrl).build();
    }

    @Override
    public boolean tieneLibros(Long editorialId) {
        return webClientLibros.get()
                .uri("/api/libros?editorialId={id}", editorialId)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("[]")
                                .map(body -> !body.equals("[]") && !body.isBlank());
                    }
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.just(false);
                    }
                    return response.createException().flatMap(Mono::error);
                })
                .block(Duration.ofSeconds(3));
    }
}