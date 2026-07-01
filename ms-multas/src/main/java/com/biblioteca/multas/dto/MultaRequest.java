package com.biblioteca.multas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class MultaRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El prestamoId es obligatorio")
    private Long prestamoId;

    @PositiveOrZero(message = "El monto debe ser mayor o igual a 0")
    private BigDecimal monto;

    @PositiveOrZero(message = "Los dias de atraso deben ser mayor o igual a 0")
    private Integer diasAtraso;

    @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
    private String motivo;

    public MultaRequest() {
    }

    public MultaRequest(Long usuarioId, Long prestamoId, BigDecimal monto, Integer diasAtraso, String motivo) {
        this.usuarioId = usuarioId;
        this.prestamoId = prestamoId;
        this.monto = monto;
        this.diasAtraso = diasAtraso;
        this.motivo = motivo;
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

    public Integer getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(Integer diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
