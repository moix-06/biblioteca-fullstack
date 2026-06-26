package com.biblioteca.libros.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(name = "anio_publicacion", nullable = false)
    private Integer anioPublicacion;

    private String idioma;

    @Column(name = "numero_paginas")
    private Integer numeroPaginas;

    private String descripcion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(name = "editorial_id", nullable = false)
    private Long editorialId;

    @ElementCollection
    @CollectionTable(name = "libro_categoria", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "categoria_id")
    private Set<Long> categoriaIds = new HashSet<>();

    public Libro() {
    }

    public Libro(Long id, String titulo, String isbn, Integer anioPublicacion, String idioma,
                 Integer numeroPaginas, String descripcion, LocalDateTime fechaRegistro,
                 Long autorId, Long editorialId, Set<Long> categoriaIds) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.anioPublicacion = anioPublicacion;
        this.idioma = idioma;
        this.numeroPaginas = numeroPaginas;
        this.descripcion = descripcion;
        this.fechaRegistro = fechaRegistro;
        this.autorId = autorId;
        this.editorialId = editorialId;
        this.categoriaIds = categoriaIds;
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