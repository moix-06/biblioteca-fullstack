package com.biblioteca.reservas.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas", indexes = {
        @Index(name = "idx_reserva_cola", columnList = "libro_id, estado, fecha_creacion")
})
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "libro_id", nullable = false)
    private Long libroId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_cumplida")
    private LocalDateTime fechaCumplida;

    @Column(name = "fecha_cancelada")
    private LocalDateTime fechaCancelada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    public Reserva() {
    }

    public Reserva(Long id, Long usuarioId, Long libroId, LocalDateTime fechaCreacion,
                   LocalDateTime fechaCumplida, LocalDateTime fechaCancelada, EstadoReserva estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaCreacion = fechaCreacion;
        this.fechaCumplida = fechaCumplida;
        this.fechaCancelada = fechaCancelada;
        this.estado = estado;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaCumplida() {
        return fechaCumplida;
    }

    public void setFechaCumplida(LocalDateTime fechaCumplida) {
        this.fechaCumplida = fechaCumplida;
    }

    public LocalDateTime getFechaCancelada() {
        return fechaCancelada;
    }

    public void setFechaCancelada(LocalDateTime fechaCancelada) {
        this.fechaCancelada = fechaCancelada;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}
