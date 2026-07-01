package com.biblioteca.reservas.controller;

import com.biblioteca.reservas.dto.ReservaRequest;
import com.biblioteca.reservas.dto.ReservaResponse;
import com.biblioteca.reservas.model.entity.EstadoReserva;
import com.biblioteca.reservas.service.ReservaService;
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
@RequestMapping("/api/reservas")
public class ReservaController {

    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    @Operation(summary = "Listar reservas",
            description = "Retorna la lista de reservas ordenadas por fecha de creacion (ASC, cola). " +
                    "Permite filtrar por libroId, usuarioId y estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente")
    })
    public ResponseEntity<List<ReservaResponse>> listar(
            @Parameter(description = "ID del libro (opcional)") @RequestParam(required = false) Long libroId,
            @Parameter(description = "ID del usuario (opcional)") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Estado de la reserva (opcional)") @RequestParam(required = false) EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.buscar(libroId, usuarioId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID",
            description = "Retorna los datos de la reserva especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaResponse> obtenerPorId(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva reserva",
            description = "Registra una nueva reserva. Valida que el usuario exista y no este bloqueado, " +
                    "que el libro exista, que el libro no tenga ejemplares disponibles (la reserva es " +
                    "para libros sin disponibilidad inmediata), y que no exista ya una reserva PENDIENTE " +
                    "del mismo usuario para el mismo libro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o Libro no encontrado"),
            @ApiResponse(responseCode = "409",
                    description = "Usuario bloqueado, libro con ejemplares disponibles, o reserva duplicada")
    })
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request) {
        logger.info("Creando reserva para usuarioId={} libroId={}",
                request.getUsuarioId(), request.getLibroId());
        return new ResponseEntity<>(reservaService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva",
            description = "Cancela una reserva en estado PENDIENTE. No se permite cancelar reservas " +
                    "ya CUMPLIDAS, CANCELADAS o EXPIRADAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "409", description = "La reserva no esta en estado PENDIENTE")
    })
    public ResponseEntity<ReservaResponse> cancelar(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        logger.info("Cancelando reserva con id={}", id);
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    @PutMapping("/{id}/cumplir")
    @Operation(summary = "Cumplir reserva",
            description = "Marca una reserva como CUMPLIDA cuando se hace efectiva. No crea el prestamo " +
                    "automaticamente; quien llama debe hacerlo via POST /api/prestamos con un ejemplarId " +
                    "disponible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cumplida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "409", description = "La reserva no esta en estado PENDIENTE")
    })
    public ResponseEntity<ReservaResponse> cumplir(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        logger.info("Cumpliendo reserva con id={}", id);
        return ResponseEntity.ok(reservaService.cumplir(id));
    }
}
