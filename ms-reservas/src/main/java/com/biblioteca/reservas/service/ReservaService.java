package com.biblioteca.reservas.service;

import com.biblioteca.reservas.client.LibroRemoto;
import com.biblioteca.reservas.client.ReservasCatalogClient;
import com.biblioteca.reservas.client.UsuarioRemoto;
import com.biblioteca.reservas.dto.ReservaRequest;
import com.biblioteca.reservas.dto.ReservaResponse;
import com.biblioteca.reservas.exception.BusinessRuleException;
import com.biblioteca.reservas.exception.ResourceNotFoundException;
import com.biblioteca.reservas.model.entity.EstadoReserva;
import com.biblioteca.reservas.model.entity.Reserva;
import com.biblioteca.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservasCatalogClient catalogClient;

    public ReservaService(ReservaRepository reservaRepository, ReservasCatalogClient catalogClient) {
        this.reservaRepository = reservaRepository;
        this.catalogClient = catalogClient;
    }

    public List<ReservaResponse> listarTodos() {
        return reservaRepository.findAll().stream()
                .map(ReservaResponse::new)
                .collect(Collectors.toList());
    }

    public List<ReservaResponse> buscar(Long libroId, Long usuarioId, EstadoReserva estado) {
        return reservaRepository.buscar(libroId, usuarioId, estado).stream()
                .map(ReservaResponse::new)
                .collect(Collectors.toList());
    }

    public ReservaResponse obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        return new ReservaResponse(reserva);
    }

    public ReservaResponse crear(ReservaRequest request) {
        UsuarioRemoto usuario = catalogClient.obtenerUsuario(request.getUsuarioId());
        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            throw new BusinessRuleException("El usuario esta bloqueado y no puede reservar");
        }

        LibroRemoto libro = catalogClient.obtenerLibro(request.getLibroId());

        int disponibles = catalogClient.contarEjemplaresDisponibles(request.getLibroId());
        if (disponibles > 0) {
            throw new BusinessRuleException(
                    "El libro tiene " + disponibles + " ejemplar(es) disponible(s); prestalo directamente");
        }

        if (reservaRepository.existsByUsuarioIdAndLibroIdAndEstado(
                request.getUsuarioId(), request.getLibroId(), EstadoReserva.PENDIENTE)) {
            throw new BusinessRuleException(
                    "Ya existe una reserva pendiente para este usuario y libro");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuarioId(request.getUsuarioId());
        reserva.setLibroId(request.getLibroId());
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        reserva = reservaRepository.save(reserva);
        return new ReservaResponse(reserva);
    }

    public ReservaResponse cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessRuleException(
                    "Solo se pueden cancelar reservas en estado PENDIENTE (estado actual: "
                            + reserva.getEstado() + ")");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setFechaCancelada(LocalDateTime.now());
        reserva = reservaRepository.save(reserva);
        return new ReservaResponse(reserva);
    }

    public ReservaResponse cumplir(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessRuleException(
                    "Solo se pueden cumplir reservas en estado PENDIENTE (estado actual: "
                            + reserva.getEstado() + ")");
        }

        reserva.setEstado(EstadoReserva.CUMPLIDA);
        reserva.setFechaCumplida(LocalDateTime.now());
        reserva = reservaRepository.save(reserva);
        return new ReservaResponse(reserva);
    }
}
