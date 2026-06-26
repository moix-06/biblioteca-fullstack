package com.biblioteca.libros.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public class LibroRequest {

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @NotNull(message = "El anio de publicacion es obligatorio")
    @Min(value = 1, message = "El anio de publicacion debe ser mayor o igual a 1")
    private Integer anioPublicacion;

    private String idioma;

    @Positive(message = "El numero de paginas debe ser positivo")
    private Integer numeroPaginas;

    private String descripcion;

    @NotNull(message = "El autorId es obligatorio")
    private Long autorId;

    @NotNull(message = "El editorialId es obligatorio")
    private Long editorialId;

    @NotEmpty(message = "Debe incluir al menos una categoria")
    private Set<@NotNull Long> categoriaIds;

    public LibroRequest() {
    }

    public LibroRequest(String titulo, String isbn, Integer anioPublicacion, String idioma,
                        Integer numeroPaginas, String descripcion, Long autorId, Long editorialId,
                        Set<Long> categoriaIds) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.anioPublicacion = anioPublicacion;
        this.idioma = idioma;
        this.numeroPaginas = numeroPaginas;
        this.descripcion = descripcion;
        this.autorId = autorId;
        this.editorialId = editorialId;
        this.categoriaIds = categoriaIds;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(Integer anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(Integer numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    public Long getEditorialId() {
        return editorialId;
    }

    public void setEditorialId(Long editorialId) {
        this.editorialId = editorialId;
    }

    public Set<Long> getCategoriaIds() {
        return categoriaIds;
    }

    public void setCategoriaIds(Set<Long> categoriaIds) {
        this.categoriaIds = categoriaIds;
    }
}