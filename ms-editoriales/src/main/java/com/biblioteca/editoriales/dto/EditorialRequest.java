package com.biblioteca.editoriales.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EditorialRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Size(max = 100, message = "El pais no puede superar 100 caracteres")
    private String pais;

    @Min(value = 1500, message = "El anio de fundacion debe ser mayor o igual a 1500")
    @Max(value = 2100, message = "El anio de fundacion no puede ser futuro")
    private Integer anioFundacion;

    @Size(max = 500, message = "El sitio web no puede superar 500 caracteres")
    private String sitioWeb;

    public EditorialRequest() {
    }

    public EditorialRequest(String nombre, String pais, Integer anioFundacion, String sitioWeb) {
        this.nombre = nombre;
        this.pais = pais;
        this.anioFundacion = anioFundacion;
        this.sitioWeb = sitioWeb;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Integer getAnioFundacion() {
        return anioFundacion;
    }

    public void setAnioFundacion(Integer anioFundacion) {
        this.anioFundacion = anioFundacion;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }
}