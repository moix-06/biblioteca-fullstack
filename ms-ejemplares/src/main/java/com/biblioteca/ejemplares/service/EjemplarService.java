package com.biblioteca.ejemplares.service;

import com.biblioteca.ejemplares.client.EjemplarCatalogClient;
import com.biblioteca.ejemplares.dto.EjemplarRequest;
import com.biblioteca.ejemplares.dto.EjemplarResponse;
import com.biblioteca.ejemplares.exception.BusinessRuleException;
import com.biblioteca.ejemplares.exception.ResourceNotFoundException;
import com.biblioteca.ejemplares.model.entity.Ejemplar;
import com.biblioteca.ejemplares.model.entity.EstadoEjemplar;
import com.biblioteca.ejemplares.repository.EjemplarRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EjemplarService {

    private static final Map<EstadoEjemplar, Set<EstadoEjemplar>> TRANSICIONES = new EnumMap<>(EstadoEjemplar.class);

    static {
        TRANSICIONES.put(EstadoEjemplar.DISPONIBLE, EnumSet.of(
                EstadoEjemplar.PRESTADO, EstadoEjemplar.RESERVADO,
                EstadoEjemplar.MANTENIMIENTO, EstadoEjemplar.BAJA));
        TRANSICIONES.put(EstadoEjemplar.PRESTADO, EnumSet.of(EstadoEjemplar.DISPONIBLE));
        TRANSICIONES.put(EstadoEjemplar.RESERVADO, EnumSet.of(
                EstadoEjemplar.DISPONIBLE, EstadoEjemplar.PRESTADO));
        TRANSICIONES.put(EstadoEjemplar.MANTENIMIENTO, EnumSet.of(
                EstadoEjemplar.DISPONIBLE, EstadoEjemplar.BAJA));
        TRANSICIONES.put(EstadoEjemplar.BAJA, EnumSet.noneOf(EstadoEjemplar.class));
    }

    private final EjemplarRepository ejemplarRepository;
    private final EjemplarCatalogClient catalogClient;

    public EjemplarService(EjemplarRepository ejemplarRepository, EjemplarCatalogClient catalogClient) {
        this.ejemplarRepository = ejemplarRepository;
        this.catalogClient = catalogClient;
    }

    public List<EjemplarResponse> listarTodos() {
        return ejemplarRepository.findAll().stream()
                .map(EjemplarResponse::new)
                .collect(Collectors.toList());
    }

    public List<EjemplarResponse> buscar(Long libroId, Long sucursalId, EstadoEjemplar estado) {
        return ejemplarRepository.buscar(libroId, sucursalId, estado).stream()
                .map(EjemplarResponse::new)
                .collect(Collectors.toList());
    }

    public EjemplarResponse obtenerPorId(Long id) {
        Ejemplar ejemplar = ejemplarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado con id: " + id));
        return new EjemplarResponse(ejemplar);
    }

    public EjemplarResponse crear(EjemplarRequest request) {
        if (ejemplarRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessRuleException("Ya existe un ejemplar con codigo: " + request.getCodigo());
        }
        catalogClient.validarLibro(request.getLibroId());
        catalogClient.validarSucursal(request.getSucursalId());

        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setCodigo(request.getCodigo());
        ejemplar.setLibroId(request.getLibroId());
        ejemplar.setSucursalId(request.getSucursalId());
        ejemplar.setEstado(request.getEstado());
        ejemplar.setUbicacion(request.getUbicacion());
        ejemplar.setObservaciones(request.getObservaciones());

        ejemplar = ejemplarRepository.save(ejemplar);
        return new EjemplarResponse(ejemplar);
    }

    public EjemplarResponse actualizar(Long id, EjemplarRequest request) {
        Ejemplar ejemplar = ejemplarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado con id: " + id));

        if (!ejemplar.getCodigo().equals(request.getCodigo())) {
            Optional<Ejemplar> existente = ejemplarRepository.findByCodigo(request.getCodigo());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new BusinessRuleException("Ya existe un ejemplar con codigo: " + request.getCodigo());
            }
        }

        catalogClient.validarLibro(request.getLibroId());
        catalogClient.validarSucursal(request.getSucursalId());

        if (ejemplar.getEstado() != request.getEstado()) {
            validarTransicion(ejemplar.getEstado(), request.getEstado());
        }

        ejemplar.setCodigo(request.getCodigo());
        ejemplar.setLibroId(request.getLibroId());
        ejemplar.setSucursalId(request.getSucursalId());
        ejemplar.setEstado(request.getEstado());
        ejemplar.setUbicacion(request.getUbicacion());
        ejemplar.setObservaciones(request.getObservaciones());

        ejemplar = ejemplarRepository.save(ejemplar);
        return new EjemplarResponse(ejemplar);
    }

    public void eliminar(Long id) {
        Ejemplar ejemplar = ejemplarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado con id: " + id));

        if (ejemplar.getEstado() == EstadoEjemplar.PRESTADO) {
            throw new BusinessRuleException(
                    "No se puede eliminar un ejemplar actualmente prestado");
        }

        ejemplarRepository.delete(ejemplar);
    }

    private void validarTransicion(EstadoEjemplar from, EstadoEjemplar to) {
        Set<EstadoEjemplar> permitidos = TRANSICIONES.getOrDefault(from, EnumSet.noneOf(EstadoEjemplar.class));
        if (!permitidos.contains(to)) {
            throw new BusinessRuleException(
                    "Transicion de estado invalida: " + from + " -> " + to);
        }
    }
}
