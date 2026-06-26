package com.biblioteca.libros.service;

import com.biblioteca.libros.client.CatalogClient;
import com.biblioteca.libros.dto.LibroRequest;
import com.biblioteca.libros.dto.LibroResponse;
import com.biblioteca.libros.exception.BusinessRuleException;
import com.biblioteca.libros.exception.ResourceNotFoundException;
import com.biblioteca.libros.model.entity.Libro;
import com.biblioteca.libros.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final CatalogClient catalogClient;

    public LibroService(LibroRepository libroRepository, CatalogClient catalogClient) {
        this.libroRepository = libroRepository;
        this.catalogClient = catalogClient;
    }

    public List<LibroResponse> listarTodos() {
        return libroRepository.findAll()
                .stream()
                .map(LibroResponse::new)
                .collect(Collectors.toList());
    }

    public LibroResponse obtenerPorId(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
        return new LibroResponse(libro);
    }

    public LibroResponse crear(LibroRequest request) {
        if (libroRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessRuleException("Ya existe un libro con ISBN: " + request.getIsbn());
        }
        if (request.getAnioPublicacion() > Year.now().getValue()) {
            throw new BusinessRuleException("El anio de publicacion no puede ser futuro");
        }
        catalogClient.validarAutor(request.getAutorId());
        catalogClient.validarEditorial(request.getEditorialId());
        request.getCategoriaIds().forEach(catalogClient::validarCategoria);

        Libro libro = new Libro();
        libro.setTitulo(request.getTitulo());
        libro.setIsbn(request.getIsbn());
        libro.setAnioPublicacion(request.getAnioPublicacion());
        libro.setIdioma(request.getIdioma());
        libro.setNumeroPaginas(request.getNumeroPaginas());
        libro.setDescripcion(request.getDescripcion());
        libro.setAutorId(request.getAutorId());
        libro.setEditorialId(request.getEditorialId());
        libro.setCategoriaIds(request.getCategoriaIds());

        libro = libroRepository.save(libro);
        return new LibroResponse(libro);
    }

    public LibroResponse actualizar(Long id, LibroRequest request) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));

        if (!libro.getIsbn().equals(request.getIsbn())) {
            Optional<Libro> existente = libroRepository.findByIsbn(request.getIsbn());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new BusinessRuleException("Ya existe un libro con ISBN: " + request.getIsbn());
            }
        }

        if (request.getAnioPublicacion() > Year.now().getValue()) {
            throw new BusinessRuleException("El anio de publicacion no puede ser futuro");
        }

        catalogClient.validarAutor(request.getAutorId());
        catalogClient.validarEditorial(request.getEditorialId());
        request.getCategoriaIds().forEach(catalogClient::validarCategoria);

        libro.setTitulo(request.getTitulo());
        libro.setIsbn(request.getIsbn());
        libro.setAnioPublicacion(request.getAnioPublicacion());
        libro.setIdioma(request.getIdioma());
        libro.setNumeroPaginas(request.getNumeroPaginas());
        libro.setDescripcion(request.getDescripcion());
        libro.setAutorId(request.getAutorId());
        libro.setEditorialId(request.getEditorialId());
        libro.setCategoriaIds(request.getCategoriaIds());

        libro = libroRepository.save(libro);
        return new LibroResponse(libro);
    }

    public void eliminar(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + id);
        }
        libroRepository.deleteById(id);
    }
}