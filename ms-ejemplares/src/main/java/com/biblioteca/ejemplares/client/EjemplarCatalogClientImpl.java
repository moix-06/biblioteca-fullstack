package com.biblioteca.ejemplares.client;

import com.biblioteca.ejemplares.exception.ResourceNotFoundException;
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
public class EjemplarCatalogClientImpl implements EjemplarCatalogClient {

    private static final Logger logger = LoggerFactory.getLogger(EjemplarCatalogClientImpl.class);

    private final WebClient webClientLibros;
    private final WebClient webClientSucursales;

    public EjemplarCatalogClientImpl(@Value("${ms.libros.url}") String librosUrl,
                                     @Value("${ms.sucursales.url}") String sucursalesUrl) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        this.webClientLibros = WebClient.builder()
                .baseUrl(librosUrl)
                .clientConnector(connector)
                .build();

        this.webClientSucursales = WebClient.builder()
                .baseUrl(sucursalesUrl)
                .clientConnector(connector)
                .build();
    }

    @Override
    public void validarLibro(Long libroId) {
        consultar(webClientLibros, "/api/libros", libroId, "Libro");
    }

    @Override
    public void validarSucursal(Long sucursalId) {
        consultar(webClientSucursales, "/api/sucursales", sucursalId, "Sucursal");
    }

    private void consultar(WebClient client, String path, Long id, String entityName) {
        try {
            client.get()
                    .uri(path + "/{id}", id)
                    .exchangeToMono(response -> {
                        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return Mono.error(new ResourceNotFoundException(
                                    entityName + " no encontrado con id: " + id));
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
                        return Mono.empty();
                    })
                    .block();
        } catch (RuntimeException ex) {
            logger.warn("Fallo al validar {} con id={}: {}", entityName, id, ex.getMessage());
            throw ex;
        }
    }
}
