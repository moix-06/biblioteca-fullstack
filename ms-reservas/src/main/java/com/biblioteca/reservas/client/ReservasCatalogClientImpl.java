package com.biblioteca.reservas.client;

import com.biblioteca.reservas.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

@Component
public class ReservasCatalogClientImpl implements ReservasCatalogClient {

    private static final Logger logger = LoggerFactory.getLogger(ReservasCatalogClientImpl.class);

    private final WebClient webClientUsuarios;
    private final WebClient webClientLibros;
    private final WebClient webClientEjemplares;

    public ReservasCatalogClientImpl(@Value("${ms.usuarios.url}") String usuariosUrl,
                                     @Value("${ms.libros.url}") String librosUrl,
                                     @Value("${ms.ejemplares.url}") String ejemplaresUrl) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        this.webClientUsuarios = WebClient.builder()
                .baseUrl(usuariosUrl)
                .clientConnector(connector)
                .build();

        this.webClientLibros = WebClient.builder()
                .baseUrl(librosUrl)
                .clientConnector(connector)
                .build();

        this.webClientEjemplares = WebClient.builder()
                .baseUrl(ejemplaresUrl)
                .clientConnector(connector)
                .build();
    }

    @Override
    public UsuarioRemoto obtenerUsuario(Long usuarioId) {
        return consultarGet(webClientUsuarios, "/api/usuarios/" + usuarioId,
                UsuarioRemoto.class, "Usuario");
    }

    @Override
    public LibroRemoto obtenerLibro(Long libroId) {
        return consultarGet(webClientLibros, "/api/libros/" + libroId,
                LibroRemoto.class, "Libro");
    }

    @Override
    public int contarEjemplaresDisponibles(Long libroId) {
        try {
            List<EjemplarRemoto> disponibles = webClientEjemplares.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/ejemplares")
                            .queryParam("libroId", libroId)
                            .queryParam("estado", "DISPONIBLE")
                            .build())
                    .exchangeToMono(response -> {
                        if (response.statusCode().isError()) {
                            return Mono.error(new WebClientResponseException(
                                    "Error al consultar ejemplares disponibles del libro " + libroId,
                                    response.statusCode().value(),
                                    response.statusCode().toString(),
                                    response.headers().asHttpHeaders(),
                                    null,
                                    null));
                        }
                        return response.bodyToMono(new ParameterizedTypeReference<List<EjemplarRemoto>>() {});
                    })
                    .block();
            return disponibles != null ? disponibles.size() : 0;
        } catch (RuntimeException ex) {
            logger.warn("Fallo al contar ejemplares disponibles del libro {}: {}",
                    libroId, ex.getMessage());
            throw ex;
        }
    }

    private <T> T consultarGet(WebClient client, String path, Class<T> type, String entityName) {
        try {
            return client.get()
                    .uri(path)
                    .exchangeToMono(response -> {
                        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return Mono.error(new ResourceNotFoundException(
                                    entityName + " no encontrado en " + path));
                        }
                        if (response.statusCode().isError()) {
                            return Mono.error(new WebClientResponseException(
                                    "Error al consultar " + path,
                                    response.statusCode().value(),
                                    response.statusCode().toString(),
                                    response.headers().asHttpHeaders(),
                                    null,
                                    null));
                        }
                        return response.bodyToMono(type);
                    })
                    .block();
        } catch (RuntimeException ex) {
            logger.warn("Fallo al llamar {}: {}", path, ex.getMessage());
            throw ex;
        }
    }
}
