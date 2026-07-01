package com.biblioteca.multas.service;

import com.biblioteca.multas.dto.MultaRequest;
import com.biblioteca.multas.dto.MultaResponse;
import com.biblioteca.multas.exception.BusinessRuleException;
import com.biblioteca.multas.exception.ResourceNotFoundException;
import com.biblioteca.multas.model.entity.EstadoMulta;
import com.biblioteca.multas.model.entity.Multa;
import com.biblioteca.multas.repository.MultaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MultaService {

    private final MultaRepository multaRepository;
    private final BigDecimal montoPorDia;
    private final BigDecimal montoMaximo;

    public MultaService(MultaRepository multaRepository,
                        @Value("${multas.monto-por-dia}") BigDecimal montoPorDia,
                        @Value("${multas.monto-maximo}") BigDecimal montoMaximo) {
        this.multaRepository = multaRepository;
        this.montoPorDia = montoPorDia;
        this.montoMaximo = montoMaximo;
    }

    public List<MultaResponse> listarTodos() {
        return multaRepository.findAll().stream()
                .map(MultaResponse::new)
                .collect(Collectors.toList());
    }

    public List<MultaResponse> buscar(Long usuarioId, Long prestamoId, EstadoMulta estado) {
        return multaRepository.buscar(usuarioId, prestamoId, estado).stream()
                .map(MultaResponse::new)
                .collect(Collectors.toList());
    }

    public MultaResponse obtenerPorId(Long id) {
        Multa multa = multaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Multa no encontrada con id: " + id));
        return new MultaResponse(multa);
    }

    public MultaResponse crear(MultaRequest request) {
        if (request.getMonto() == null && request.getDiasAtraso() == null) {
            throw new BusinessRuleException(
                    "Debe proporcionar monto o diasAtraso para crear la multa");
        }

        BigDecimal montoFinal = calcularMonto(request);

        if (multaRepository.existsByPrestamoIdAndEstado(request.getPrestamoId(), EstadoMulta.PENDIENTE)) {
            throw new BusinessRuleException(
                    "Ya existe una multa pendiente para el prestamo: " + request.getPrestamoId());
        }

        Multa multa = new Multa();
        multa.setUsuarioId(request.getUsuarioId());
        multa.setPrestamoId(request.getPrestamoId());
        multa.setMonto(montoFinal);
        multa.setMotivo(request.getMotivo());
        multa.setFechaCreacion(LocalDateTime.now());
        multa.setEstado(EstadoMulta.PENDIENTE);

        multa = multaRepository.save(multa);
        return new MultaResponse(multa);
    }

    public MultaResponse pagar(Long id) {
        Multa multa = multaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Multa no encontrada con id: " + id));

        if (multa.getEstado() != EstadoMulta.PENDIENTE) {
            throw new BusinessRuleException(
                    "Solo se pueden pagar multas en estado PENDIENTE (estado actual: "
                            + multa.getEstado() + ")");
        }

        multa.setEstado(EstadoMulta.PAGADA);
        multa.setFechaPago(LocalDateTime.now());
        multa = multaRepository.save(multa);
        return new MultaResponse(multa);
    }

    private BigDecimal calcularMonto(MultaRequest request) {
        if (request.getMonto() != null) {
            return cap(request.getMonto());
        }
        BigDecimal calculado = montoPorDia.multiply(BigDecimal.valueOf(request.getDiasAtraso()));
        return cap(calculado);
    }

    private BigDecimal cap(BigDecimal value) {
        return value.min(montoMaximo);
    }
}
