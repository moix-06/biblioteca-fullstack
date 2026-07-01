package com.biblioteca.prestamos.service;

import com.biblioteca.prestamos.client.EjemplarRemoto;
import com.biblioteca.prestamos.client.PrestamosCatalogClient;
import com.biblioteca.prestamos.client.UsuarioRemoto;
import com.biblioteca.prestamos.dto.PrestamoRequest;
import com.biblioteca.prestamos.dto.PrestamoResponse;
import com.biblioteca.prestamos.exception.BusinessRuleException;
import com.biblioteca.prestamos.exception.ResourceNotFoundException;
import com.biblioteca.prestamos.model.entity.EstadoPrestamo;
import com.biblioteca.prestamos.model.entity.Prestamo;
import com.biblioteca.prestamos.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoService {

    private static final List<EstadoPrestamo> ESTADOS_ACTIVOS_OWING =
            List.of(EstadoPrestamo.ACTIVO, EstadoPrestamo.VENCIDO);

    private final PrestamoRepository prestamoRepository;
    private final PrestamosCatalogClient catalogClient;
    private final int duracionDias;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           PrestamosCatalogClient catalogClient,
                           @Value("${prestamos.duracion-dias:14}") int duracionDias) {
        this.prestamoRepository = prestamoRepository;
        this.catalogClient = catalogClient;
        this.duracionDias = duracionDias;
    }

    public List<PrestamoResponse> listarTodos() {
        return prestamoRepository.findAll().stream()
                .map(PrestamoResponse::new)
                .collect(Collectors.toList());
    }

    public List<PrestamoResponse> buscar(Long usuarioId, EstadoPrestamo estado) {
        return prestamoRepository.buscar(usuarioId, estado).stream()
                .map(PrestamoResponse::new)
                .collect(Collectors.toList());
    }

    public PrestamoResponse obtenerPorId(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + id));
        return new PrestamoResponse(prestamo);
    }

    public PrestamoResponse crear(PrestamoRequest request) {
        UsuarioRemoto usuario = catalogClient.obtenerUsuario(request.getUsuarioId());
        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            throw new BusinessRuleException("El usuario esta bloqueado y no puede realizar prestamos");
        }

        Integer limite = catalogClient.obtenerLimiteUsuario(request.getUsuarioId());
        long vigentes = prestamoRepository.countByUsuarioIdAndEstadoIn(
                request.getUsuarioId(), ESTADOS_ACTIVOS_OWING);
        if (vigentes >= limite) {
            throw new BusinessRuleException(
                    "El usuario alcanzo el limite de prestamos (" + limite + ")");
        }

        EjemplarRemoto ejemplar = catalogClient.obtenerEjemplar(request.getEjemplarId());
        if (!"DISPONIBLE".equals(ejemplar.getEstado())) {
            throw new BusinessRuleException(
                    "El ejemplar no esta disponible (estado actual: " + ejemplar.getEstado() + ")");
        }

        if (prestamoRepository.existsByUsuarioIdAndEjemplarIdAndEstado(
                request.getUsuarioId(), request.getEjemplarId(), EstadoPrestamo.ACTIVO)) {
            throw new BusinessRuleException(
                    "Ya existe un prestamo activo para este usuario y ejemplar");
        }

        LocalDateTime ahora = LocalDateTime.now();
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuarioId(request.getUsuarioId());
        prestamo.setEjemplarId(request.getEjemplarId());
        prestamo.setFechaPrestamo(ahora);
        prestamo.setFechaDevolucionPrevista(ahora.plusDays(duracionDias));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        prestamo.setObservaciones(request.getObservaciones());

        prestamo = prestamoRepository.save(prestamo);

        try {
            catalogClient.cambiarEstadoEjemplar(request.getEjemplarId(), "PRESTADO");
        } catch (RuntimeException ex) {
            prestamoRepository.delete(prestamo);
            throw ex;
        }

        return new PrestamoResponse(prestamo);
    }

    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + id));

        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new BusinessRuleException(
                    "Solo se pueden devolver prestamos en estado ACTIVO (estado actual: "
                            + prestamo.getEstado() + ")");
        }

        LocalDateTime ahora = LocalDateTime.now();
        prestamo.setFechaDevolucionReal(ahora);
        prestamo.setEstado(ahora.isAfter(prestamo.getFechaDevolucionPrevista())
                ? EstadoPrestamo.VENCIDO
                : EstadoPrestamo.DEVUELTO);

        catalogClient.cambiarEstadoEjemplar(prestamo.getEjemplarId(), "DISPONIBLE");

        prestamo = prestamoRepository.save(prestamo);
        return new PrestamoResponse(prestamo);
    }

    public PrestamoResponse cancelar(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + id));

        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new BusinessRuleException(
                    "Solo se pueden cancelar prestamos en estado ACTIVO (estado actual: "
                            + prestamo.getEstado() + ")");
        }

        prestamo.setEstado(EstadoPrestamo.CANCELADO);
        prestamo.setFechaDevolucionReal(LocalDateTime.now());

        catalogClient.cambiarEstadoEjemplar(prestamo.getEjemplarId(), "DISPONIBLE");

        prestamo = prestamoRepository.save(prestamo);
        return new PrestamoResponse(prestamo);
    }
}
