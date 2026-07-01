package com.biblioteca.sucursales.service;

import com.biblioteca.sucursales.dto.SucursalRequest;
import com.biblioteca.sucursales.dto.SucursalResponse;
import com.biblioteca.sucursales.exception.BusinessRuleException;
import com.biblioteca.sucursales.exception.ResourceNotFoundException;
import com.biblioteca.sucursales.model.entity.Sucursal;
import com.biblioteca.sucursales.repository.SucursalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public SucursalService(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    public List<SucursalResponse> listarTodos() {
        return sucursalRepository.findAll()
                .stream()
                .map(SucursalResponse::new)
                .collect(Collectors.toList());
    }

    public SucursalResponse obtenerPorId(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
        return new SucursalResponse(sucursal);
    }

    public SucursalResponse crear(SucursalRequest request) {
        validarUnicidad(request.getNombre(), null);

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setCiudad(request.getCiudad());
        sucursal.setComuna(request.getComuna());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setEmail(request.getEmail());

        sucursal = sucursalRepository.save(sucursal);
        return new SucursalResponse(sucursal);
    }

    public SucursalResponse actualizar(Long id, SucursalRequest request) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));

        validarUnicidad(request.getNombre(), id);

        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setCiudad(request.getCiudad());
        sucursal.setComuna(request.getComuna());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setEmail(request.getEmail());

        sucursal = sucursalRepository.save(sucursal);
        return new SucursalResponse(sucursal);
    }

    public void eliminar(Long id) {
        if (!sucursalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + id);
        }
        sucursalRepository.deleteById(id);
    }

    private void validarUnicidad(String nombre, Long idExcluir) {
        Optional<Sucursal> existente = sucursalRepository.findByNombreIgnoreCase(nombre);
        if (existente.isPresent() && (idExcluir == null || !existente.get().getId().equals(idExcluir))) {
            throw new BusinessRuleException("Ya existe una sucursal con nombre: " + nombre);
        }
    }
}
