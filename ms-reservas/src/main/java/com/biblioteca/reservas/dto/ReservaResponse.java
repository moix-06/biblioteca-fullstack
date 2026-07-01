package com.biblioteca.reservas.dto;

import com.biblioteca.reservas.model.entity.EstadoReserva;
import com.biblioteca.reservas.model.entity.Reserva;

import java.time.LocalDateTime;

public class ReservaResponse {

    private Long id;
    private Long usuarioId;
    private Long libroId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCumplida;
    private LocalDateTime fechaCancelada;
    private EstadoReserva estado;

    public ReservaResponse() {
    }

    public ReservaResponse(Reserva reserva) {
        this.id = reserva.getId();
        this.usuarioId = reserva.getUsuarioId();
        this.libroId = reserva.getLibroId();
        this.fechaCreacion = reserva.getFechaCreacion();
        this.fechaCumplida = reserva.getFechaCumplida();
        this.fechaCancelada = reserva.getFechaCancelada();
        this.estado = reserva.getEstado();
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
