package com.biblioteca.reservas.client;

public interface ReservasCatalogClient {

    UsuarioRemoto obtenerUsuario(Long usuarioId);

    LibroRemoto obtenerLibro(Long libroId);

    int contarEjemplaresDisponibles(Long libroId);
}
