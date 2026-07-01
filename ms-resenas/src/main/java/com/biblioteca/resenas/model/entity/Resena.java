package com.biblioteca.resenas.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "resenas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_resena_usuario_libro", columnNames = {"usuario_id", "libro_id"})
})
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "libro_id", nullable = false)
    private Long libroId;

    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 2000)
    private String comentario;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_edicion")
    private LocalDateTime fechaEdicion;

    public Resena() {
    }

    public Resena(Long id, Long usuarioId, Long libroId, Integer calificacion, String comentario,
                  LocalDateTime fechaCreacion, LocalDateTime fechaEdicion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaCreacion = fechaCreacion;
        this.fechaEdicion = fechaEdicion;
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

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(LocalDateTime fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }
}
