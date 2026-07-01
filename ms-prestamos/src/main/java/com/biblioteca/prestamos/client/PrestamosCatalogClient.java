package com.biblioteca.prestamos.client;

public interface PrestamosCatalogClient {

    UsuarioRemoto obtenerUsuario(Long usuarioId);

    Integer obtenerLimiteUsuario(Long usuarioId);

    EjemplarRemoto obtenerEjemplar(Long ejemplarId);

    void cambiarEstadoEjemplar(Long ejemplarId, String nuevoEstado);
}
