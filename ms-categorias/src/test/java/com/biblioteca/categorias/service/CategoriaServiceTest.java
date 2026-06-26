package com.biblioteca.categorias.service;

import com.biblioteca.categorias.dto.CategoriaRequest;
import com.biblioteca.categorias.dto.CategoriaResponse;
import com.biblioteca.categorias.exception.BusinessRuleException;
import com.biblioteca.categorias.exception.ResourceNotFoundException;
import com.biblioteca.categorias.model.entity.Categoria;
import com.biblioteca.categorias.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private CategoriaRequest buildValidRequest() {
        CategoriaRequest req = new CategoriaRequest();
        req.setNombre("Novela");
        req.setDescripcion("Obras narrativas de ficcion");
        return req;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("crearCategoria")
    class CrearCategoriaTests {

        @Test
        @DisplayName("datosValidos_guardaCategoria")
        void datosValidos_guardaCategoria() {
            CategoriaRequest req = buildValidRequest();
            when(categoriaRepository.findByNombreIgnoreCase(req.getNombre())).thenReturn(Optional.empty());
            when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> {
                Categoria c = inv.getArgument(0);
                c.setId(10L);
                return c;
            });

            CategoriaResponse resp = categoriaService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(req.getNombre(), resp.getNombre());
            assertEquals(req.getDescripcion(), resp.getDescripcion());
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        @DisplayName("duplicadoCaseInsensitive_lanzaExcepcion")
        void duplicadoCaseInsensitive_lanzaExcepcion() {
            CategoriaRequest req = buildValidRequest();
            Categoria existente = new Categoria();
            existente.setId(99L);
            existente.setNombre("NOVELA");
            when(categoriaRepository.findByNombreIgnoreCase(req.getNombre())).thenReturn(Optional.of(existente));

            assertThrows(BusinessRuleException.class, () -> categoriaService.crear(req));
            verify(categoriaRepository, never()).save(any(Categoria.class));
        }
    }

    @Nested
    @DisplayName("obtenerCategoria")
    class ObtenerCategoriaTests {

        @Test
        @DisplayName("existente_retornaCategoria")
        void existente_retornaCategoria() {
            Categoria categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNombre("Ciencia ficcion");
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            CategoriaResponse resp = categoriaService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals("Ciencia ficcion", resp.getNombre());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> categoriaService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("actualizarCategoria")
    class ActualizarCategoriaTests {

        @Test
        @DisplayName("existente_actualiza")
        void existente_actualiza() {
            Categoria categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNombre("Vieja");
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(categoriaRepository.findByNombreIgnoreCase(any())).thenReturn(Optional.empty());
            when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

            CategoriaRequest req = buildValidRequest();
            CategoriaResponse resp = categoriaService.actualizar(1L, req);

            assertEquals(req.getNombre(), resp.getNombre());
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        @DisplayName("duplicadoEnOtraCategoria_lanzaExcepcion")
        void duplicadoEnOtraCategoria_lanzaExcepcion() {
            Categoria categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNombre("Vieja");
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            Categoria otra = new Categoria();
            otra.setId(2L);
            otra.setNombre("Novela");
            when(categoriaRepository.findByNombreIgnoreCase("Novela")).thenReturn(Optional.of(otra));

            CategoriaRequest req = buildValidRequest();

            assertThrows(BusinessRuleException.class, () -> categoriaService.actualizar(1L, req));
            verify(categoriaRepository, never()).save(any(Categoria.class));
        }

        @Test
        @DisplayName("mismoNombreEnMismaCategoria_noLanzaExcepcion")
        void mismoNombreEnMismaCategoria_noLanzaExcepcion() {
            Categoria categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNombre("Novela");
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(categoriaRepository.findByNombreIgnoreCase("Novela")).thenReturn(Optional.of(categoria));
            when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

            CategoriaRequest req = buildValidRequest();
            CategoriaResponse resp = categoriaService.actualizar(1L, req);

            assertNotNull(resp);
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> categoriaService.actualizar(99L, buildValidRequest()));
        }
    }

    @Nested
    @DisplayName("eliminarCategoria")
    class EliminarCategoriaTests {

        @Test
        @DisplayName("existente_elimina")
        void existente_elimina() {
            when(categoriaRepository.existsById(1L)).thenReturn(true);

            categoriaService.eliminar(1L);

            verify(categoriaRepository).deleteById(1L);
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(categoriaRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> categoriaService.eliminar(99L));
            verify(categoriaRepository, never()).deleteById(any(Long.class));
        }
    }

    @Test
    @DisplayName("listarTodos_retornaLista")
    void listarTodos_retornaLista() {
        Categoria c1 = new Categoria();
        c1.setId(1L);
        c1.setNombre("A");
        Categoria c2 = new Categoria();
        c2.setId(2L);
        c2.setNombre("B");
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        var result = categoriaService.listarTodos();

        assertEquals(2, result.size());
    }
}