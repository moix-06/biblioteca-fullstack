package com.biblioteca.usuarios.dto;

import com.biblioteca.usuarios.model.entity.Rol;

import java.time.LocalDateTime;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private Rol rol;
    private Boolean bloqueado;
    private LocalDateTime fechaRegistro;
    private int limitePrestamos;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String nombre, String email, String telefono, String direccion, Rol rol, Boolean bloqueado, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.rol = rol;
        this.bloqueado = bloqueado;
        this.fechaRegistro = fechaRegistro;
        this.limitePrestamos = rol.getLimitePrestamos();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getLimitePrestamos() {
        return limitePrestamos;
    }

    public void setLimitePrestamos(int limitePrestamos) {
        this.limitePrestamos = limitePrestamos;
    }
}