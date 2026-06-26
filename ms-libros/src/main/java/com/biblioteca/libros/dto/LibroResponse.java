package com.biblioteca.libros.dto;

import com.biblioteca.libros.model.entity.Libro;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class LibroResponse {

    private Long id;
    private String titulo;
    private String isbn;
    private Integer anioPublicacion;
    private String idioma;
    private Integer numeroPaginas;
    private String descripcion;
    private LocalDateTime fechaRegistro;
    private Long autorId;
    private Long editorialId;
    private Set<Long> categoriaIds;

    public LibroResponse() {
    }

    public LibroResponse(Libro libro) {
        this.id = libro.getId();
        this.titulo = libro.getTitulo();
        this.isbn = libro.getIsbn();
        this.anioPublicacion = libro.getAnioPublicacion();
        this.idioma = libro.getIdioma();
        this.numeroPaginas = libro.getNumeroPaginas();
        this.descripcion = libro.getDescripcion();
        this.fechaRegistro = libro.getFechaRegistro();
        this.autorId = libro.getAutorId();
        this.editorialId = libro.getEditorialId();
        this.categoriaIds = libro.getCategoriaIds() != null ? libro.getCategoriaIds() : new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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