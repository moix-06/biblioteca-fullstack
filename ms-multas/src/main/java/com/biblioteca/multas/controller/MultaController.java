package com.biblioteca.multas.controller;

import com.biblioteca.multas.dto.MultaRequest;
import com.biblioteca.multas.dto.MultaResponse;
import com.biblioteca.multas.model.entity.EstadoMulta;
import com.biblioteca.multas.service.MultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/multas")
public class MultaController {

    private static final Logger logger = LoggerFactory.getLogger(MultaController.class);

    private final MultaService multaService;

    public MultaController(MultaService multaService) {
        this.multaService = multaService;
    }

    @GetMapping
    @Operation(summary = "Listar multas",
            description = "Retorna la lista de multas ordenadas por fecha de creacion (DESC). " +
                    "Permite filtrar por usuarioId, prestamoId y estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de multas obtenida exitosamente")
    })
    public ResponseEntity<List<MultaResponse>> listar(
            @Parameter(description = "ID del usuario (opcional)") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "ID del prestamo (opcional)") @RequestParam(required = false) Long prestamoId,
            @Parameter(description = "Estado de la multa (opcional)") @RequestParam(required = false) EstadoMulta estado) {
        return ResponseEntity.ok(multaService.buscar(usuarioId, prestamoId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener multa por ID",
            description = "Retorna los datos de la multa especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multa encontrada"),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada")
    })
    public ResponseEntity<MultaResponse> obtenerPorId(
            @Parameter(description = "ID de la multa") @PathVariable Long id) {
        return ResponseEntity.ok(multaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva multa",
            description = "Registra una nueva multa. El monto puede ser provisto directamente o " +
                    "calculado como diasAtraso * multas.monto-por-dia (capeado por multas.monto-maximo). " +
                    "No se permite crear una multa PENDIENTE para un prestamo que ya tenga una.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Multa creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409",
                    description = "Falta monto y diasAtraso, o ya existe multa pendiente para el prestamo")
    })
    public ResponseEntity<MultaResponse> crear(@Valid @RequestBody MultaRequest request) {
        logger.info("Creando multa para usuarioId={} prestamoId={} monto={} diasAtraso={}",
                request.getUsuarioId(), request.getPrestamoId(), request.getMonto(), request.getDiasAtraso());
        return new ResponseEntity<>(multaService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/pagar")
    @Operation(summary = "Pagar multa",
            description = "Marca una multa en estado PENDIENTE como PAGADA, registrando la fecha de pago. " +
                    "No se permite pagar multas ya PAGADAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multa pagada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada"),
            @ApiResponse(responseCode = "409", description = "La multa no esta en estado PENDIENTE")
    })
    public ResponseEntity<MultaResponse> pagar(
            @Parameter(description = "ID de la multa") @PathVariable Long id) {
        logger.info("Pagando multa con id={}", id);
        return ResponseEntity.ok(multaService.pagar(id));
    }
}
