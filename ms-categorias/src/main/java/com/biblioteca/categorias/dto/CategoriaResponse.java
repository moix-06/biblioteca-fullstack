package com.biblioteca.categorias.dto;

import com.biblioteca.categorias.model.entity.Categoria;

import java.time.LocalDateTime;

public class CategoriaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaRegistro;

    public CategoriaResponse() {
    }

    public CategoriaResponse(Categoria categoria) {
        this.id = categoria.getId();
        this.nombre = categoria.getNombre();
        this.descripcion = categoria.getDescripcion();
        this.fechaRegistro = categoria.getFechaRegistro();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
}