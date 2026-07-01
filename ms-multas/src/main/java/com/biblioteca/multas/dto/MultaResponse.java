package com.biblioteca.multas.dto;

import com.biblioteca.multas.model.entity.EstadoMulta;
import com.biblioteca.multas.model.entity.Multa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MultaResponse {

    private Long id;
    private Long usuarioId;
    private Long prestamoId;
    private BigDecimal monto;
    private String motivo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPago;
    private EstadoMulta estado;

    public MultaResponse() {
    }

    public MultaResponse(Multa multa) {
        this.id = multa.getId();
        this.usuarioId = multa.getUsuarioId();
        this.prestamoId = multa.getPrestamoId();
        this.monto = multa.getMonto();
        this.motivo = multa.getMotivo();
        this.fechaCreacion = multa.getFechaCreacion();
        this.fechaPago = multa.getFechaPago();
        this.estado = multa.getEstado();
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

    public Long getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(Long prestamoId) {
        this.prestamoId = prestamoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public EstadoMulta getEstado() {
        return estado;
    }

    public void setEstado(EstadoMulta estado) {
        this.estado = estado;
    }
}
