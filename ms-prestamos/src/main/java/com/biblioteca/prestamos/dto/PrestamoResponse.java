package com.biblioteca.prestamos.dto;

import com.biblioteca.prestamos.model.entity.EstadoPrestamo;
import com.biblioteca.prestamos.model.entity.Prestamo;

import java.time.LocalDateTime;

public class PrestamoResponse {

    private Long id;
    private Long usuarioId;
    private Long ejemplarId;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucionPrevista;
    private LocalDateTime fechaDevolucionReal;
    private EstadoPrestamo estado;
    private String observaciones;
    private boolean vencido;

    public PrestamoResponse() {
    }

    public PrestamoResponse(Prestamo prestamo) {
        this.id = prestamo.getId();
        this.usuarioId = prestamo.getUsuarioId();
        this.ejemplarId = prestamo.getEjemplarId();
        this.fechaPrestamo = prestamo.getFechaPrestamo();
        this.fechaDevolucionPrevista = prestamo.getFechaDevolucionPrevista();
        this.fechaDevolucionReal = prestamo.getFechaDevolucionReal();
        this.estado = prestamo.getEstado();
        this.observaciones = prestamo.getObservaciones();
        this.vencido = calcularVencido(prestamo);
    }

    private boolean calcularVencido(Prestamo prestamo) {
        return prestamo.getFechaDevolucionPrevista() != null
                && prestamo.getFechaDevolucionPrevista().isBefore(LocalDateTime.now());
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

    public Long getEjemplarId() {
        return ejemplarId;
    }

    public void setEjemplarId(Long ejemplarId) {
        this.ejemplarId = ejemplarId;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDateTime fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucionPrevista() {
        return fechaDevolucionPrevista;
    }

    public void setFechaDevolucionPrevista(LocalDateTime fechaDevolucionPrevista) {
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }

    public LocalDateTime getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(LocalDateTime fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isVencido() {
        return vencido;
    }

    public void setVencido(boolean vencido) {
        this.vencido = vencido;
    }
}
