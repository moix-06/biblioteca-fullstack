package com.biblioteca.editoriales.dto;

import com.biblioteca.editoriales.model.entity.Editorial;

import java.time.LocalDateTime;

public class EditorialResponse {

    private Long id;
    private String nombre;
    private String pais;
    private Integer anioFundacion;
    private String sitioWeb;
    private LocalDateTime fechaRegistro;

    public EditorialResponse() {
    }

    public EditorialResponse(Editorial editorial) {
        this.id = editorial.getId();
        this.nombre = editorial.getNombre();
        this.pais = editorial.getPais();
        this.anioFundacion = editorial.getAnioFundacion();
        this.sitioWeb = editorial.getSitioWeb();
        this.fechaRegistro = editorial.getFechaRegistro();
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}