package com.biblioteca.ejemplares.dto;

import com.biblioteca.ejemplares.model.entity.EstadoEjemplar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EjemplarRequest {

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotNull(message = "El libroId es obligatorio")
    private Long libroId;

    @NotNull(message = "El sucursalId es obligatorio")
    private Long sucursalId;

    @NotNull(message = "El estado es obligatorio")
    private EstadoEjemplar estado;

    @Size(max = 100, message = "La ubicacion no puede superar los 100 caracteres")
    private String ubicacion;

    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    private String observaciones;

    public EjemplarRequest() {
    }

    public EjemplarRequest(String codigo, Long libroId, Long sucursalId, EstadoEjemplar estado,
                           String ubicacion, String observaciones) {
        this.codigo = codigo;
        this.libroId = libroId;
        this.sucursalId = sucursalId;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.observaciones = observaciones;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public EstadoEjemplar getEstado() {
        return estado;
    }

    public void setEstado(EstadoEjemplar estado) {
        this.estado = estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
