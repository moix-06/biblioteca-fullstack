package com.biblioteca.categorias.controller;

import com.biblioteca.categorias.dto.CategoriaRequest;
import com.biblioteca.categorias.dto.CategoriaResponse;
import com.biblioteca.categorias.service.CategoriaService;
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
@RequestMapping("/api/categorias")
public class CategoriaController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las categorias", description = "Retorna una lista de todas las categorias registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorias obtenida exitosamente")
    })
    public ResponseEntity<List<CategoriaResponse>> listarTodos() {
        return ResponseEntity.ok(categoriaService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoria por ID", description = "Retorna los datos de la categoria especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<CategoriaResponse> obtenerPorId(
            @Parameter(description = "ID de la categoria") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoria", description = "Registra una nueva categoria en el catalogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Categoria duplicada")
    })
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        return new ResponseEntity<>(categoriaService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoria", description = "Actualiza los datos de una categoria existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada"),
            @ApiResponse(responseCode = "409", description = "Categoria duplicada")
    })
    public ResponseEntity<CategoriaResponse> actualizar(
            @Parameter(description = "ID de la categoria") @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoria", description = "Elimina una categoria del catalogo. La integridad con libros la mantiene ms-libros; este servicio no la verifica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la categoria") @PathVariable Long id) {
        logger.info("Eliminando categoria con id={}", id);
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}