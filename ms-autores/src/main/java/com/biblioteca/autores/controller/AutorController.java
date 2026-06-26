package com.biblioteca.autores.controller;

import com.biblioteca.autores.dto.AutorRequest;
import com.biblioteca.autores.dto.AutorResponse;
import com.biblioteca.autores.service.AutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private static final Logger logger = LoggerFactory.getLogger(AutorController.class);

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los autores", description = "Retorna una lista de todos los autores registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de autores obtenida exitosamente")
    })
    public ResponseEntity<List<AutorResponse>> listarTodos() {
        return ResponseEntity.ok(autorService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener autor por ID", description = "Retorna los datos del autor especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autor encontrado"),
            @ApiResponse(responseCode = "404", description = "Autor no encontrado")
    })
    public ResponseEntity<AutorResponse> obtenerPorId(
            @Parameter(description = "ID del autor") @PathVariable Long id) {
        return ResponseEntity.ok(autorService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo autor", description = "Registra un nuevo autor en el catalogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Autor creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Autor duplicado o fecha invalida")
    })
    public ResponseEntity<AutorResponse> crear(@Valid @RequestBody AutorRequest request) {
        return new ResponseEntity<>(autorService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar autor", description = "Actualiza los datos de un autor existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autor actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Autor no encontrado"),
            @ApiResponse(responseCode = "409", description = "Autor duplicado o fecha invalida")
    })
    public ResponseEntity<AutorResponse> actualizar(
            @Parameter(description = "ID del autor") @PathVariable Long id,
            @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(autorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar autor", description = "Elimina un autor del catalogo si no tiene libros asociados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Autor eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Autor no encontrado"),
            @ApiResponse(responseCode = "409", description = "El autor tiene libros asociados"),
            @ApiResponse(responseCode = "503", description = "ms-libros no disponible")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del autor") @PathVariable Long id) {
        logger.info("Eliminando autor con id={}", id);
        autorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}