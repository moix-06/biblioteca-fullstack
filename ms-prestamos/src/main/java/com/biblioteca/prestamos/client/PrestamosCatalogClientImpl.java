package com.biblioteca.prestamos.client;

import com.biblioteca.prestamos.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
public class PrestamosCatalogClientImpl implements PrestamosCatalogClient {

    private static final Logger logger = LoggerFactory.getLogger(PrestamosCatalogClientImpl.class);

    private final WebClient webClientUsuarios;
    private final WebClient webClientEjemplares;

    public PrestamosCatalogClientImpl(@Value("${ms.usuarios.url}") String usuariosUrl,
                                      @Value("${ms.ejemplares.url}") String ejemplaresUrl) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        this.webClientUsuarios = WebClient.builder()
                .baseUrl(usuariosUrl)
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
    public Integer obtenerLimiteUsuario(Long usuarioId) {
        return consultarGet(webClientUsuarios, "/api/usuarios/" + usuarioId + "/limite",
                Integer.class, "LimiteUsuario");
    }

    @Override
    public EjemplarRemoto obtenerEjemplar(Long ejemplarId) {
        return consultarGet(webClientEjemplares, "/api/ejemplares/" + ejemplarId,
                EjemplarRemoto.class, "Ejemplar");
    }

    @Override
    public void cambiarEstadoEjemplar(Long ejemplarId, String nuevoEstado) {
        EjemplarRemoto actual = obtenerEjemplar(ejemplarId);
        actual.setEstado(nuevoEstado);
        try {
            webClientEjemplares.put()
                    .uri("/api/ejemplares/" + ejemplarId)
                    .bodyValue(actual)
                    .exchangeToMono(response -> {
                        if (response.statusCode().isError()) {
                            return Mono.error(new WebClientResponseException(
                                    "Error al actualizar estado del ejemplar " + ejemplarId,
                                    response.statusCode().value(),
                                    response.statusCode().toString(),
                                    response.headers().asHttpHeaders(),
                                    null,
                                    null));
                        }
                        return Mono.empty();
                    })
                    .block();
        } catch (RuntimeException ex) {
            logger.warn("Fallo al cambiar estado del ejemplar {} a {}: {}",
                    ejemplarId, nuevoEstado, ex.getMessage());
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
