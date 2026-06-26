package com.biblioteca.editoriales.controller;

import com.biblioteca.editoriales.dto.EditorialRequest;
import com.biblioteca.editoriales.dto.EditorialResponse;
import com.biblioteca.editoriales.service.EditorialService;
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
@RequestMapping("/api/editoriales")
public class EditorialController {

    private static final Logger logger = LoggerFactory.getLogger(EditorialController.class);

    private final EditorialService editorialService;

    public EditorialController(EditorialService editorialService) {
        this.editorialService = editorialService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las editoriales", description = "Retorna una lista de todas las editoriales registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de editoriales obtenida exitosamente")
    })
    public ResponseEntity<List<EditorialResponse>> listarTodos() {
        return ResponseEntity.ok(editorialService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener editorial por ID", description = "Retorna los datos de la editorial especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Editorial encontrada"),
            @ApiResponse(responseCode = "404", description = "Editorial no encontrada")
    })
    public ResponseEntity<EditorialResponse> obtenerPorId(
            @Parameter(description = "ID de la editorial") @PathVariable Long id) {
        return ResponseEntity.ok(editorialService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva editorial", description = "Registra una nueva editorial en el catalogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Editorial creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Editorial duplicada o anio invalido")
    })
    public ResponseEntity<EditorialResponse> crear(@Valid @RequestBody EditorialRequest request) {
        return new ResponseEntity<>(editorialService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar editorial", description = "Actualiza los datos de una editorial existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Editorial actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Editorial no encontrada"),
            @ApiResponse(responseCode = "409", description = "Editorial duplicada o anio invalido")
    })
    public ResponseEntity<EditorialResponse> actualizar(
            @Parameter(description = "ID de la editorial") @PathVariable Long id,
            @Valid @RequestBody EditorialRequest request) {
        return ResponseEntity.ok(editorialService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar editorial", description = "Elimina una editorial del catalogo si no tiene libros asociados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Editorial eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Editorial no encontrada"),
            @ApiResponse(responseCode = "409", description = "La editorial tiene libros asociados"),
            @ApiResponse(responseCode = "503", description = "ms-libros no disponible")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la editorial") @PathVariable Long id) {
        logger.info("Eliminando editorial con id={}", id);
        editorialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}