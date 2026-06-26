package com.biblioteca.libros.controller;

import com.biblioteca.libros.dto.LibroRequest;
import com.biblioteca.libros.dto.LibroResponse;
import com.biblioteca.libros.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);
    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los libros", description = "Retorna una lista de todos los libros del catalogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente")
    })
    public ResponseEntity<List<LibroResponse>> listarTodos() {
        return ResponseEntity.ok(libroService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener libro por ID", description = "Retorna los datos del libro especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    public ResponseEntity<LibroResponse> obtenerPorId(
            @Parameter(description = "ID del libro") @PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo libro", description = "Registra un nuevo libro en el catalogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Autor/Editorial/Categoria no encontrado"),
            @ApiResponse(responseCode = "409", description = "ISBN duplicado o anio futuro")
    })
    public ResponseEntity<LibroResponse> crear(
            @Valid @RequestBody LibroRequest request) {
        return new ResponseEntity<>(libroService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar libro", description = "Actualiza los datos de un libro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Libro/Autor/Editorial/Categoria no encontrado"),
            @ApiResponse(responseCode = "409", description = "ISBN duplicado o anio futuro")
    })
    public ResponseEntity<LibroResponse> actualizar(
            @Parameter(description = "ID del libro") @PathVariable Long id,
            @Valid @RequestBody LibroRequest request) {
        return ResponseEntity.ok(libroService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar libro", description = "Elimina un libro del catalogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Libro eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del libro") @PathVariable Long id) {
        logger.info("Eliminando libro con id={}", id);
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}