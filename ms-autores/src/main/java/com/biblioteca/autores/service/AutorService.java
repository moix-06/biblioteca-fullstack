package com.biblioteca.autores.service;

import com.biblioteca.autores.client.LibroLookupClient;
import com.biblioteca.autores.dto.AutorRequest;
import com.biblioteca.autores.dto.AutorResponse;
import com.biblioteca.autores.exception.BusinessRuleException;
import com.biblioteca.autores.exception.ResourceNotFoundException;
import com.biblioteca.autores.model.entity.Autor;
import com.biblioteca.autores.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorService {

    private static final int ANIO_MINIMO = 1500;

    private final AutorRepository autorRepository;
    private final LibroLookupClient libroLookupClient;

    public AutorService(AutorRepository autorRepository, LibroLookupClient libroLookupClient) {
        this.autorRepository = autorRepository;
        this.libroLookupClient = libroLookupClient;
    }

    public List<AutorResponse> listarTodos() {
        return autorRepository.findAll()
                .stream()
                .map(AutorResponse::new)
                .collect(Collectors.toList());
    }

    public AutorResponse obtenerPorId(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con id: " + id));
        return new AutorResponse(autor);
    }

    public AutorResponse crear(AutorRequest request) {
        validarFechaNacimiento(request.getFechaNacimiento());
        validarUnicidad(request.getNombre(), request.getApellido(), null);

        Autor autor = new Autor();
        autor.setNombre(request.getNombre());
        autor.setApellido(request.getApellido());
        autor.setFechaNacimiento(request.getFechaNacimiento());
        autor.setNacionalidad(request.getNacionalidad());
        autor.setBiografia(request.getBiografia());

        autor = autorRepository.save(autor);
        return new AutorResponse(autor);
    }

    public AutorResponse actualizar(Long id, AutorRequest request) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con id: " + id));

        validarFechaNacimiento(request.getFechaNacimiento());
        validarUnicidad(request.getNombre(), request.getApellido(), id);

        autor.setNombre(request.getNombre());
        autor.setApellido(request.getApellido());
        autor.setFechaNacimiento(request.getFechaNacimiento());
        autor.setNacionalidad(request.getNacionalidad());
        autor.setBiografia(request.getBiografia());

        autor = autorRepository.save(autor);
        return new AutorResponse(autor);
    }

    public void eliminar(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Autor no encontrado con id: " + id);
        }
        if (libroLookupClient.tieneLibros(id)) {
            throw new BusinessRuleException("No se puede eliminar el autor: tiene libros asociados");
        }
        autorRepository.deleteById(id);
    }

    private void validarFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return;
        }
        if (fechaNacimiento.getYear() < ANIO_MINIMO) {
            throw new BusinessRuleException("La fecha de nacimiento no puede ser anterior al ano " + ANIO_MINIMO);
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new BusinessRuleException("La fecha de nacimiento no puede ser futura");
        }
    }

    private void validarUnicidad(String nombre, String apellido, Long idExcluir) {
        Optional<Autor> existente = autorRepository
                .findByNombreIgnoreCaseAndApellidoIgnoreCase(nombre, apellido);
        if (existente.isPresent() && (idExcluir == null || !existente.get().getId().equals(idExcluir))) {
            throw new BusinessRuleException("Ya existe un autor con nombre y apellido: " + nombre + " " + apellido);
        }
    }
}