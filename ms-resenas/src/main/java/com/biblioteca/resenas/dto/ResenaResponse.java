package com.biblioteca.resenas.dto;

import com.biblioteca.resenas.model.entity.Resena;

import java.time.LocalDateTime;

public class ResenaResponse {

    private Long id;
    private Long usuarioId;
    private Long libroId;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    public ResenaResponse() {
    }

    public ResenaResponse(Resena resena) {
        this.id = resena.getId();
        this.usuarioId = resena.getUsuarioId();
        this.libroId = resena.getLibroId();
        this.calificacion = resena.getCalificacion();
        this.comentario = resena.getComentario();
        this.fechaCreacion = resena.getFechaCreacion();
        this.fechaEdicion = resena.getFechaEdicion();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(LocalDateTime fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }
}
