package com.biblioteca.editoriales.service;

import com.biblioteca.editoriales.client.LibroLookupClient;
import com.biblioteca.editoriales.dto.EditorialRequest;
import com.biblioteca.editoriales.dto.EditorialResponse;
import com.biblioteca.editoriales.exception.BusinessRuleException;
import com.biblioteca.editoriales.exception.ResourceNotFoundException;
import com.biblioteca.editoriales.model.entity.Editorial;
import com.biblioteca.editoriales.repository.EditorialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Year;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditorialServiceTest {

    @Mock
    private EditorialRepository editorialRepository;

    @Mock
    private LibroLookupClient libroLookupClient;

    @InjectMocks
    private EditorialService editorialService;

    private EditorialRequest buildValidRequest() {
        EditorialRequest req = new EditorialRequest();
        req.setNombre("Editorial Sudamericana");
        req.setPais("Argentina");
        req.setAnioFundacion(1939);
        req.setSitioWeb("https://www.sudamericana.com");
        return req;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("crearEditorial")
    class CrearEditorialTests {

        @Test
        @DisplayName("datosValidos_guardaEditorial")
        void datosValidos_guardaEditorial() {
            EditorialRequest req = buildValidRequest();
            when(editorialRepository.findByNombreIgnoreCase(req.getNombre())).thenReturn(Optional.empty());
            when(editorialRepository.save(any(Editorial.class))).thenAnswer(inv -> {
                Editorial e = inv.getArgument(0);
                e.setId(10L);
                return e;
            });

            EditorialResponse resp = editorialService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(req.getNombre(), resp.getNombre());
            assertEquals(req.getAnioFundacion(), resp.getAnioFundacion());
            verify(editorialRepository).save(any(Editorial.class));
        }

        @Test
        @DisplayName("duplicadoCaseInsensitive_lanzaExcepcion")
        void duplicadoCaseInsensitive_lanzaExcepcion() {
            EditorialRequest req = buildValidRequest();
            Editorial existente = new Editorial();
            existente.setId(99L);
            existente.setNombre("EDITORIAL SUDAMERICANA");
            when(editorialRepository.findByNombreIgnoreCase(req.getNombre())).thenReturn(Optional.of(existente));

            assertThrows(BusinessRuleException.class, () -> editorialService.crear(req));
            verify(editorialRepository, never()).save(any(Editorial.class));
        }

        @Test
        @DisplayName("anioFuturo_lanzaExcepcion")
        void anioFuturo_lanzaExcepcion() {
            EditorialRequest req = buildValidRequest();
            req.setAnioFundacion(Year.now().getValue() + 1);
            when(editorialRepository.findByNombreIgnoreCase(req.getNombre())).thenReturn(Optional.empty());

            assertThrows(BusinessRuleException.class, () -> editorialService.crear(req));
            verify(editorialRepository, never()).save(any(Editorial.class));
        }

        @Test
        @DisplayName("anioAnteriorA1500_lanzaExcepcion")
        void anioAnteriorA1500_lanzaExcepcion() {
            EditorialRequest req = buildValidRequest();
            req.setAnioFundacion(1400);
            when(editorialRepository.findByNombreIgnoreCase(req.getNombre())).thenReturn(Optional.empty());

            assertThrows(BusinessRuleException.class, () -> editorialService.crear(req));
            verify(editorialRepository, never()).save(any(Editorial.class));
        }

        @Test
        @DisplayName("anioNulo_noValida_yGuarda")
        void anioNulo_noValida_yGuarda() {
            EditorialRequest req = buildValidRequest();
            req.setAnioFundacion(null);
            when(editorialRepository.findByNombreIgnoreCase(req.getNombre())).thenReturn(Optional.empty());
            when(editorialRepository.save(any(Editorial.class))).thenAnswer(inv -> {
                Editorial e = inv.getArgument(0);
                e.setId(1L);
                return e;
            });

            EditorialResponse resp = editorialService.crear(req);

            assertNotNull(resp.getId());
            verify(editorialRepository).save(any(Editorial.class));
        }
    }

    @Nested
    @DisplayName("obtenerEditorial")
    class ObtenerEditorialTests {

        @Test
        @DisplayName("existente_retornaEditorial")
        void existente_retornaEditorial() {
            Editorial editorial = new Editorial();
            editorial.setId(1L);
            editorial.setNombre("Planeta");
            when(editorialRepository.findById(1L)).thenReturn(Optional.of(editorial));

            EditorialResponse resp = editorialService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals("Planeta", resp.getNombre());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(editorialRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> editorialService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("actualizarEditorial")
    class ActualizarEditorialTests {

        @Test
        @DisplayName("existente_actualiza")
        void existente_actualiza() {
            Editorial editorial = new Editorial();
            editorial.setId(1L);
            editorial.setNombre("Vieja");
            when(editorialRepository.findById(1L)).thenReturn(Optional.of(editorial));
            when(editorialRepository.findByNombreIgnoreCase(any())).thenReturn(Optional.empty());
            when(editorialRepository.save(any(Editorial.class))).thenAnswer(inv -> inv.getArgument(0));

            EditorialRequest req = buildValidRequest();
            EditorialResponse resp = editorialService.actualizar(1L, req);

            assertEquals(req.getNombre(), resp.getNombre());
            verify(editorialRepository).save(any(Editorial.class));
        }

        @Test
        @DisplayName("duplicadoEnOtraEditorial_lanzaExcepcion")
        void duplicadoEnOtraEditorial_lanzaExcepcion() {
            Editorial editorial = new Editorial();
            editorial.setId(1L);
            editorial.setNombre("Vieja");
            when(editorialRepository.findById(1L)).thenReturn(Optional.of(editorial));

            Editorial otra = new Editorial();
            otra.setId(2L);
            otra.setNombre("Editorial Sudamericana");
            when(editorialRepository.findByNombreIgnoreCase("Editorial Sudamericana"))
                    .thenReturn(Optional.of(otra));

            EditorialRequest req = buildValidRequest();

            assertThrows(BusinessRuleException.class, () -> editorialService.actualizar(1L, req));
            verify(editorialRepository, never()).save(any(Editorial.class));
        }

        @Test
        @DisplayName("mismoNombreEnMismaEditorial_noLanzaExcepcion")
        void mismoNombreEnMismaEditorial_noLanzaExcepcion() {
            Editorial editorial = new Editorial();
            editorial.setId(1L);
            editorial.setNombre("Editorial Sudamericana");
            when(editorialRepository.findById(1L)).thenReturn(Optional.of(editorial));
            when(editorialRepository.findByNombreIgnoreCase("Editorial Sudamericana"))
                    .thenReturn(Optional.of(editorial));
            when(editorialRepository.save(any(Editorial.class))).thenAnswer(inv -> inv.getArgument(0));

            EditorialRequest req = buildValidRequest();
            EditorialResponse resp = editorialService.actualizar(1L, req);

            assertNotNull(resp);
            verify(editorialRepository).save(any(Editorial.class));
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(editorialRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> editorialService.actualizar(99L, buildValidRequest()));
        }
    }

    @Nested
    @DisplayName("eliminarEditorial")
    class EliminarEditorialTests {

        @Test
        @DisplayName("existenteSinLibros_elimina")
        void existenteSinLibros_elimina() {
            when(editorialRepository.existsById(1L)).thenReturn(true);
            when(libroLookupClient.tieneLibros(1L)).thenReturn(false);

            editorialService.eliminar(1L);

            verify(editorialRepository).deleteById(1L);
        }

        @Test
        @DisplayName("existenteConLibros_lanzaExcepcion")
        void existenteConLibros_lanzaExcepcion() {
            when(editorialRepository.existsById(1L)).thenReturn(true);
            when(libroLookupClient.tieneLibros(1L)).thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> editorialService.eliminar(1L));
            verify(editorialRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(editorialRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> editorialService.eliminar(99L));
            verify(editorialRepository, never()).deleteById(anyLong());
        }
    }

    @Test
    @DisplayName("listarTodos_retornaLista")
    void listarTodos_retornaLista() {
        Editorial e1 = new Editorial();
        e1.setId(1L);
        e1.setNombre("A");
        Editorial e2 = new Editorial();
        e2.setId(2L);
        e2.setNombre("B");
        when(editorialRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        var result = editorialService.listarTodos();

        assertEquals(2, result.size());
    }
}