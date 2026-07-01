package com.biblioteca.sucursales.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SucursalRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 250, message = "La direccion no puede superar los 250 caracteres")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String ciudad;

    @Size(max = 100, message = "La comuna no puede superar los 100 caracteres")
    private String comuna;

    @Pattern(regexp = "^[+0-9\\-\\s()]+$",
            message = "El telefono solo puede contener digitos, espacios, '+', '-', '(' y ')'")
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    private String telefono;

    @Email(message = "El email debe tener un formato valido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    public SucursalRequest() {
    }

    public SucursalRequest(String nombre, String direccion, String ciudad, String comuna,
                           String telefono, String email) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.comuna = comuna;
        this.telefono = telefono;
        this.email = email;
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
}
