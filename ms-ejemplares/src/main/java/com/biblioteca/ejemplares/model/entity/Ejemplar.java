package com.biblioteca.ejemplares.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ejemplares")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(name = "libro_id", nullable = false)
    private Long libroId;

    @Column(name = "sucursal_id", nullable = false)
    private Long sucursalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEjemplar estado;

    private String ubicacion;

    private String observaciones;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso = LocalDateTime.now();

    public Ejemplar() {
    }

    public Ejemplar(Long id, String codigo, Long libroId, Long sucursalId, EstadoEjemplar estado,
                    String ubicacion, String observaciones, LocalDateTime fechaIngreso) {
        this.id = id;
        this.codigo = codigo;
        this.libroId = libroId;
        this.sucursalId = sucursalId;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.observaciones = observaciones;
        this.fechaIngreso = fechaIngreso;
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
