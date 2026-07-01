package com.biblioteca.ejemplares.dto;

import com.biblioteca.ejemplares.model.entity.Ejemplar;
import com.biblioteca.ejemplares.model.entity.EstadoEjemplar;

import java.time.LocalDateTime;

public class EjemplarResponse {

    private Long id;
    private String codigo;
    private Long libroId;
    private Long sucursalId;
    private EstadoEjemplar estado;
    private String ubicacion;
    private String observaciones;
    private LocalDateTime fechaIngreso;

    public EjemplarResponse() {
    }

    public EjemplarResponse(Ejemplar ejemplar) {
        this.id = ejemplar.getId();
        this.codigo = ejemplar.getCodigo();
        this.libroId = ejemplar.getLibroId();
        this.sucursalId = ejemplar.getSucursalId();
        this.estado = ejemplar.getEstado();
        this.ubicacion = ejemplar.getUbicacion();
        this.observaciones = ejemplar.getObservaciones();
        this.fechaIngreso = ejemplar.getFechaIngreso();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}
