package com.biblioteca.editoriales.service;

import com.biblioteca.editoriales.client.LibroLookupClient;
import com.biblioteca.editoriales.dto.EditorialRequest;
import com.biblioteca.editoriales.dto.EditorialResponse;
import com.biblioteca.editoriales.exception.BusinessRuleException;
import com.biblioteca.editoriales.exception.ResourceNotFoundException;
import com.biblioteca.editoriales.model.entity.Editorial;
import com.biblioteca.editoriales.repository.EditorialRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EditorialService {

    private static final int ANIO_MINIMO = 1500;

    private final EditorialRepository editorialRepository;
    private final LibroLookupClient libroLookupClient;

    public EditorialService(EditorialRepository editorialRepository, LibroLookupClient libroLookupClient) {
        this.editorialRepository = editorialRepository;
        this.libroLookupClient = libroLookupClient;
    }

    public List<EditorialResponse> listarTodos() {
        return editorialRepository.findAll()
                .stream()
                .map(EditorialResponse::new)
                .collect(Collectors.toList());
    }

    public EditorialResponse obtenerPorId(Long id) {
        Editorial editorial = editorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada con id: " + id));
        return new EditorialResponse(editorial);
    }

    public EditorialResponse crear(EditorialRequest request) {
        validarAnioFundacion(request.getAnioFundacion());
        validarUnicidad(request.getNombre(), null);

        Editorial editorial = new Editorial();
        editorial.setNombre(request.getNombre());
        editorial.setPais(request.getPais());
        editorial.setAnioFundacion(request.getAnioFundacion());
        editorial.setSitioWeb(request.getSitioWeb());

        editorial = editorialRepository.save(editorial);
        return new EditorialResponse(editorial);
    }

    public EditorialResponse actualizar(Long id, EditorialRequest request) {
        Editorial editorial = editorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial no encontrada con id: " + id));

        validarAnioFundacion(request.getAnioFundacion());
        validarUnicidad(request.getNombre(), id);

        editorial.setNombre(request.getNombre());
        editorial.setPais(request.getPais());
        editorial.setAnioFundacion(request.getAnioFundacion());
        editorial.setSitioWeb(request.getSitioWeb());

        editorial = editorialRepository.save(editorial);
        return new EditorialResponse(editorial);
    }

    public void eliminar(Long id) {
        if (!editorialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Editorial no encontrada con id: " + id);
        }
        if (libroLookupClient.tieneLibros(id)) {
            throw new BusinessRuleException("No se puede eliminar la editorial: tiene libros asociados");
        }
        editorialRepository.deleteById(id);
    }

    private void validarAnioFundacion(Integer anioFundacion) {
        if (anioFundacion == null) {
            return;
        }
        if (anioFundacion < ANIO_MINIMO) {
            throw new BusinessRuleException("El anio de fundacion no puede ser anterior a " + ANIO_MINIMO);
        }
        if (anioFundacion > Year.now().getValue()) {
            throw new BusinessRuleException("El anio de fundacion no puede ser futuro");
        }
    }

    private void validarUnicidad(String nombre, Long idExcluir) {
        Optional<Editorial> existente = editorialRepository.findByNombreIgnoreCase(nombre);
        if (existente.isPresent() && (idExcluir == null || !existente.get().getId().equals(idExcluir))) {
            throw new BusinessRuleException("Ya existe una editorial con nombre: " + nombre);
        }
    }
}