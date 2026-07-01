package com.biblioteca.ejemplares.controller;

import com.biblioteca.ejemplares.dto.EjemplarRequest;
import com.biblioteca.ejemplares.dto.EjemplarResponse;
import com.biblioteca.ejemplares.model.entity.EstadoEjemplar;
import com.biblioteca.ejemplares.service.EjemplarService;
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
@RequestMapping("/api/ejemplares")
public class EjemplarController {

    private static final Logger logger = LoggerFactory.getLogger(EjemplarController.class);

    private final EjemplarService ejemplarService;

    public EjemplarController(EjemplarService ejemplarService) {
        this.ejemplarService = ejemplarService;
    }

    @GetMapping
    @Operation(summary = "Listar ejemplares",
            description = "Retorna la lista de ejemplares. Permite filtrar por libroId, sucursalId y estado. " +
                    "Si no se envian filtros, retorna todos los ejemplares.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ejemplares obtenida exitosamente")
    })
    public ResponseEntity<List<EjemplarResponse>> listar(
            @Parameter(description = "ID del libro (opcional)") @RequestParam(required = false) Long libroId,
            @Parameter(description = "ID de la sucursal (opcional)") @RequestParam(required = false) Long sucursalId,
            @Parameter(description = "Estado del ejemplar (opcional)") @RequestParam(required = false) EstadoEjemplar estado) {
        return ResponseEntity.ok(ejemplarService.buscar(libroId, sucursalId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ejemplar por ID",
            description = "Retorna los datos del ejemplar especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ejemplar encontrado"),
            @ApiResponse(responseCode = "404", description = "Ejemplar no encontrado")
    })
    public ResponseEntity<EjemplarResponse> obtenerPorId(
            @Parameter(description = "ID del ejemplar") @PathVariable Long id) {
        return ResponseEntity.ok(ejemplarService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo ejemplar",
            description = "Registra un nuevo ejemplar (copia fisica) en el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ejemplar creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Libro o Sucursal no encontrado"),
            @ApiResponse(responseCode = "409", description = "Codigo duplicado")
    })
    public ResponseEntity<EjemplarResponse> crear(@Valid @RequestBody EjemplarRequest request) {
        logger.info("Creando ejemplar con codigo={}", request.getCodigo());
        return new ResponseEntity<>(ejemplarService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ejemplar",
            description = "Actualiza los datos de un ejemplar existente, incluyendo su estado (con validacion de transicion)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ejemplar actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Ejemplar/Libro/Sucursal no encontrado"),
            @ApiResponse(responseCode = "409", description = "Codigo duplicado o transicion de estado invalida")
    })
    public ResponseEntity<EjemplarResponse> actualizar(
            @Parameter(description = "ID del ejemplar") @PathVariable Long id,
            @Valid @RequestBody EjemplarRequest request) {
        return ResponseEntity.ok(ejemplarService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ejemplar",
            description = "Elimina un ejemplar del inventario. No se permite si esta actualmente prestado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ejemplar eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ejemplar no encontrado"),
            @ApiResponse(responseCode = "409", description = "El ejemplar esta prestado y no puede eliminarse")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del ejemplar") @PathVariable Long id) {
        logger.info("Eliminando ejemplar con id={}", id);
        ejemplarService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
