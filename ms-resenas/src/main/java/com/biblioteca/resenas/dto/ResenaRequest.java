package com.biblioteca.resenas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResenaRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El libroId es obligatorio")
    private Long libroId;

    @NotNull(message = "La calificacion es obligatoria")
    @Min(value = 1, message = "La calificacion debe ser al menos 1")
    @Max(value = 5, message = "La calificacion debe ser a lo sumo 5")
    private Integer calificacion;

    @Size(max = 2000, message = "El comentario no puede superar los 2000 caracteres")
    private String comentario;

    public ResenaRequest() {
    }

    public ResenaRequest(Long usuarioId, Long libroId, Integer calificacion, String comentario) {
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.calificacion = calificacion;
        this.comentario = comentario;
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
}
