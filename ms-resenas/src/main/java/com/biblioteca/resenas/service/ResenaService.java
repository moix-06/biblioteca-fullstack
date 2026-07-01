package com.biblioteca.resenas.service;

import com.biblioteca.resenas.dto.ResenaRequest;
import com.biblioteca.resenas.dto.ResenaResponse;
import com.biblioteca.resenas.exception.BusinessRuleException;
import com.biblioteca.resenas.exception.ResourceNotFoundException;
import com.biblioteca.resenas.model.entity.Resena;
import com.biblioteca.resenas.repository.ResenaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;

    public ResenaService(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }

    public List<ResenaResponse> listarTodos() {
        return resenaRepository.findAll().stream()
                .map(ResenaResponse::new)
                .collect(Collectors.toList());
    }

    public List<ResenaResponse> buscar(Long libroId, Long usuarioId, Integer calificacion) {
        return resenaRepository.buscar(libroId, usuarioId, calificacion).stream()
                .map(ResenaResponse::new)
                .collect(Collectors.toList());
    }

    public ResenaResponse obtenerPorId(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con id: " + id));
        return new ResenaResponse(resena);
    }

    public ResenaResponse crear(ResenaRequest request) {
        if (resenaRepository.existsByUsuarioIdAndLibroId(request.getUsuarioId(), request.getLibroId())) {
            throw new BusinessRuleException(
                    "Ya existe una resena para este usuario y libro");
        }

        Resena resena = new Resena();
        resena.setUsuarioId(request.getUsuarioId());
        resena.setLibroId(request.getLibroId());
        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());
        resena.setFechaCreacion(LocalDateTime.now());
        resena.setFechaEdicion(null);

        resena = resenaRepository.save(resena);
        return new ResenaResponse(resena);
    }

    public ResenaResponse actualizar(Long id, ResenaRequest request) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con id: " + id));

        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());
        resena.setFechaEdicion(LocalDateTime.now());

        resena = resenaRepository.save(resena);
        return new ResenaResponse(resena);
    }

    public void eliminar(Long id) {
        if (!resenaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resena no encontrada con id: " + id);
        }
        resenaRepository.deleteById(id);
    }
}
