package com.biblioteca.resenas.controller;

import com.biblioteca.resenas.dto.ResenaRequest;
import com.biblioteca.resenas.dto.ResenaResponse;
import com.biblioteca.resenas.service.ResenaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private static final Logger logger = LoggerFactory.getLogger(ResenaController.class);

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    @Operation(summary = "Listar resenas",
            description = "Retorna la lista de resenas ordenadas por fecha de creacion (DESC). " +
                    "Permite filtrar por libroId, usuarioId y calificacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de resenas obtenida exitosamente")
    })
    public ResponseEntity<List<ResenaResponse>> listar(
            @Parameter(description = "ID del libro (opcional)") @RequestParam(required = false) Long libroId,
            @Parameter(description = "ID del usuario (opcional)") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Calificacion exacta (opcional, 1-5)") @RequestParam(required = false) Integer calificacion) {
        return ResponseEntity.ok(resenaService.buscar(libroId, usuarioId, calificacion));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener resena por ID",
            description = "Retorna los datos de la resena especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resena encontrada"),
            @ApiResponse(responseCode = "404", description = "Resena no encontrada")
    })
    public ResponseEntity<ResenaResponse> obtenerPorId(
            @Parameter(description = "ID de la resena") @PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva resena",
            description = "Registra una resena (calificacion y comentario) de un libro por un usuario. " +
                    "No se permite crear mas de una resena por (usuarioId, libroId).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resena creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (calificacion fuera de 1-5 o comentario demasiado largo)"),
            @ApiResponse(responseCode = "409", description = "Ya existe una resena para este usuario y libro")
    })
    public ResponseEntity<ResenaResponse> crear(@Valid @RequestBody ResenaRequest request) {
        logger.info("Creando resena para usuarioId={} libroId={} calificacion={}",
                request.getUsuarioId(), request.getLibroId(), request.getCalificacion());
        return new ResponseEntity<>(resenaService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar resena",
            description = "Edita la calificacion y/o comentario de una resena existente. " +
                    "No se puede cambiar el usuarioId ni el libroId. La fecha de edicion se actualiza automaticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resena actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (calificacion fuera de 1-5 o comentario demasiado largo)"),
            @ApiResponse(responseCode = "404", description = "Resena no encontrada")
    })
    public ResponseEntity<ResenaResponse> actualizar(
            @Parameter(description = "ID de la resena") @PathVariable Long id,
            @Valid @RequestBody ResenaRequest request) {
        logger.info("Actualizando resena con id={}", id);
        return ResponseEntity.ok(resenaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar resena",
            description = "Elimina una resena del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resena eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Resena no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la resena") @PathVariable Long id) {
        logger.info("Eliminando resena con id={}", id);
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
