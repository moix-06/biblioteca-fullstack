package com.biblioteca.libros.client;

public interface CatalogClient {
    void validarAutor(Long autorId);
    void validarEditorial(Long editorialId);
    void validarCategoria(Long categoriaId);
}