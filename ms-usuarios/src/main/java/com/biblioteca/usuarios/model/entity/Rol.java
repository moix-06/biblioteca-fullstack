package com.biblioteca.usuarios.model.entity;

public enum Rol {
    ALUMNO(3),
    DOCENTE(5),
    BIBLIOTECARIO(10);

    private final int limitePrestamos;

    Rol(int limitePrestamos) {
        this.limitePrestamos = limitePrestamos;
    }

    public int getLimitePrestamos() {
        return limitePrestamos;
    }
}