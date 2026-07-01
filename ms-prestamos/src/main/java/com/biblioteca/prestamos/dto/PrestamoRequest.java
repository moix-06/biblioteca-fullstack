package com.biblioteca.prestamos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PrestamoRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ejemplarId es obligatorio")
    private Long ejemplarId;

    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    private String observaciones;

    public PrestamoRequest() {
    }

    public PrestamoRequest(Long usuarioId, Long ejemplarId, String observaciones) {
        this.usuarioId = usuarioId;
        this.ejemplarId = ejemplarId;
        this.observaciones = observaciones;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getEjemplarId() {
        return ejemplarId;
    }

    public void setEjemplarId(Long ejemplarId) {
        this.ejemplarId = ejemplarId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
