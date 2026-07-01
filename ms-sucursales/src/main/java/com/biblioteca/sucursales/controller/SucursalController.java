package com.biblioteca.sucursales.controller;

import com.biblioteca.sucursales.dto.SucursalRequest;
import com.biblioteca.sucursales.dto.SucursalResponse;
import com.biblioteca.sucursales.service.SucursalService;
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
@RequestMapping("/api/sucursales")
public class SucursalController {

    private static final Logger logger = LoggerFactory.getLogger(SucursalController.class);

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las sucursales",
            description = "Retorna una lista de todas las sucursales registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sucursales obtenida exitosamente")
    })
    public ResponseEntity<List<SucursalResponse>> listarTodos() {
        return ResponseEntity.ok(sucursalService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sucursal por ID",
            description = "Retorna los datos de la sucursal especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucursal encontrada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    public ResponseEntity<SucursalResponse> obtenerPorId(
            @Parameter(description = "ID de la sucursal") @PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva sucursal",
            description = "Registra una nueva sucursal (sede) en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucursal creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Nombre de sucursal duplicado")
    })
    public ResponseEntity<SucursalResponse> crear(@Valid @RequestBody SucursalRequest request) {
        logger.info("Creando sucursal con nombre={}", request.getNombre());
        return new ResponseEntity<>(sucursalService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sucursal",
            description = "Actualiza los datos de una sucursal existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucursal actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada"),
            @ApiResponse(responseCode = "409", description = "Nombre de sucursal duplicado")
    })
    public ResponseEntity<SucursalResponse> actualizar(
            @Parameter(description = "ID de la sucursal") @PathVariable Long id,
            @Valid @RequestBody SucursalRequest request) {
        return ResponseEntity.ok(sucursalService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sucursal",
            description = "Elimina una sucursal del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sucursal eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la sucursal") @PathVariable Long id) {
        logger.info("Eliminando sucursal con id={}", id);
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
