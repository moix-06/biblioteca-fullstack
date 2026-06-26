package com.biblioteca.categorias.service;

import com.biblioteca.categorias.dto.CategoriaRequest;
import com.biblioteca.categorias.dto.CategoriaResponse;
import com.biblioteca.categorias.exception.BusinessRuleException;
import com.biblioteca.categorias.exception.ResourceNotFoundException;
import com.biblioteca.categorias.model.entity.Categoria;
import com.biblioteca.categorias.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponse> listarTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaResponse::new)
                .collect(Collectors.toList());
    }

    public CategoriaResponse obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + id));
        return new CategoriaResponse(categoria);
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        validarUnicidad(request.getNombre(), null);

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        categoria = categoriaRepository.save(categoria);
        return new CategoriaResponse(categoria);
    }

    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + id));

        validarUnicidad(request.getNombre(), id);

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        categoria = categoriaRepository.save(categoria);
        return new CategoriaResponse(categoria);
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    private void validarUnicidad(String nombre, Long idExcluir) {
        Optional<Categoria> existente = categoriaRepository.findByNombreIgnoreCase(nombre);
        if (existente.isPresent() && (idExcluir == null || !existente.get().getId().equals(idExcluir))) {
            throw new BusinessRuleException("Ya existe una categoria con nombre: " + nombre);
        }
    }
}