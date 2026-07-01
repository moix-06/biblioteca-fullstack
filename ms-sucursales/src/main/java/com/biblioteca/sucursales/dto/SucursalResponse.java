package com.biblioteca.sucursales.dto;

import com.biblioteca.sucursales.model.entity.Sucursal;

import java.time.LocalDateTime;

public class SucursalResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String comuna;
    private String telefono;
    private String email;
    private LocalDateTime fechaRegistro;

    public SucursalResponse() {
    }

    public SucursalResponse(Sucursal sucursal) {
        this.id = sucursal.getId();
        this.nombre = sucursal.getNombre();
        this.direccion = sucursal.getDireccion();
        this.ciudad = sucursal.getCiudad();
        this.comuna = sucursal.getComuna();
        this.telefono = sucursal.getTelefono();
        this.email = sucursal.getEmail();
        this.fechaRegistro = sucursal.getFechaRegistro();
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
