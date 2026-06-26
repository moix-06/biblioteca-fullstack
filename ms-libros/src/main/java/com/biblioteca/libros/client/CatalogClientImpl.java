package com.biblioteca.libros.client;

import com.biblioteca.libros.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class CatalogClientImpl implements CatalogClient {

    private final WebClient webClientAutores;
    private final WebClient webClientEditoriales;
    private final WebClient webClientCategorias;

    public CatalogClientImpl(@Value("${ms.autores.url}") String autoresUrl,
                             @Value("${ms.editoriales.url}") String editorialesUrl,
                             @Value("${ms.categorias.url}") String categoriasUrl) {
        this.webClientAutores = WebClient.builder().baseUrl(autoresUrl).build();
        this.webClientEditoriales = WebClient.builder().baseUrl(editorialesUrl).build();
        this.webClientCategorias = WebClient.builder().baseUrl(categoriasUrl).build();
    }

    @Override
    public void validarAutor(Long autorId) {
        consultar(webClientAutores, "/api/autores", autorId, "Autor");
    }

    @Override
    public void validarEditorial(Long editorialId) {
        consultar(webClientEditoriales, "/api/editoriales", editorialId, "Editorial");
    }

    @Override
    public void validarCategoria(Long categoriaId) {
        consultar(webClientCategorias, "/api/categorias", categoriaId, "Categoria");
    }

    private void consultar(WebClient client, String path, Long id, String entityName) {
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
    }
}