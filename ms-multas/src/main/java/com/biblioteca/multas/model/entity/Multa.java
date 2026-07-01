package com.biblioteca.multas.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "multas", indexes = {
        @Index(name = "idx_multa_prestamo_estado", columnList = "prestamo_id, estado")
})
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "prestamo_id", nullable = false)
    private Long prestamoId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(length = 500)
    private String motivo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMulta estado;

    public Multa() {
    }

    public Multa(Long id, Long usuarioId, Long prestamoId, BigDecimal monto, String motivo,
                 LocalDateTime fechaCreacion, LocalDateTime fechaPago, EstadoMulta estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.prestamoId = prestamoId;
        this.monto = monto;
        this.motivo = motivo;
        this.fechaCreacion = fechaCreacion;
        this.fechaPago = fechaPago;
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
