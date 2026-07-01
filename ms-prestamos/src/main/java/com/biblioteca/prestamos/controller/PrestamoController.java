package com.biblioteca.prestamos.controller;

import com.biblioteca.prestamos.dto.PrestamoRequest;
import com.biblioteca.prestamos.dto.PrestamoResponse;
import com.biblioteca.prestamos.model.entity.EstadoPrestamo;
import com.biblioteca.prestamos.service.PrestamoService;
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
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private static final Logger logger = LoggerFactory.getLogger(PrestamoController.class);

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    @Operation(summary = "Listar prestamos",
            description = "Retorna la lista de prestamos. Permite filtrar por usuarioId y estado. " +
                    "Si no se envian filtros, retorna todos los prestamos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de prestamos obtenida exitosamente")
    })
    public ResponseEntity<List<PrestamoResponse>> listar(
            @Parameter(description = "ID del usuario (opcional)") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Estado del prestamo (opcional)") @RequestParam(required = false) EstadoPrestamo estado) {
        return ResponseEntity.ok(prestamoService.buscar(usuarioId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener prestamo por ID",
            description = "Retorna los datos del prestamo especificado, incluyendo el flag 'vencido' " +
                    "calculado al vuelo si el prestamo esta activo y su fecha prevista ya vencio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prestamo encontrado"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado")
    })
    public ResponseEntity<PrestamoResponse> obtenerPorId(
            @Parameter(description = "ID del prestamo") @PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo prestamo",
            description = "Registra un nuevo prestamo validando que el usuario exista y no este bloqueado, " +
                    "que no haya superado su limite, y que el ejemplar este DISPONIBLE. Luego cambia el estado " +
                    "del ejemplar a PRESTADO. Si la operacion remota falla, hace rollback del prestamo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prestamo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o Ejemplar no encontrado"),
            @ApiResponse(responseCode = "409",
                    description = "Usuario bloqueado, limite alcanzado, ejemplar no disponible o duplicado")
    })
    public ResponseEntity<PrestamoResponse> crear(@Valid @RequestBody PrestamoRequest request) {
        logger.info("Creando prestamo para usuarioId={} ejemplarId={}",
                request.getUsuarioId(), request.getEjemplarId());
        return new ResponseEntity<>(prestamoService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/devolver")
    @Operation(summary = "Devolver prestamo",
            description = "Marca el prestamo como DEVUELTO (o VENCIDO si la devolucion es posterior a la " +
                    "fecha prevista) y libera el ejemplar (estado DISPONIBLE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prestamo devuelto exitosamente"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado"),
            @ApiResponse(responseCode = "409",
                    description = "El prestamo no esta en estado ACTIVO (ya fue devuelto o cancelado)")
    })
    public ResponseEntity<PrestamoResponse> devolver(
            @Parameter(description = "ID del prestamo") @PathVariable Long id) {
        logger.info("Devolviendo prestamo con id={}", id);
        return ResponseEntity.ok(prestamoService.devolver(id));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar prestamo",
            description = "Cancela un prestamo en estado ACTIVO y libera el ejemplar. " +
                    "No se permite cancelar prestamos ya DEVUELTOS o CANCELADOS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prestamo cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado"),
            @ApiResponse(responseCode = "409",
                    description = "El prestamo no esta en estado ACTIVO (ya fue devuelto o cancelado)")
    })
    public ResponseEntity<PrestamoResponse> cancelar(
            @Parameter(description = "ID del prestamo") @PathVariable Long id) {
        logger.info("Cancelando prestamo con id={}", id);
        return ResponseEntity.ok(prestamoService.cancelar(id));
    }
}
