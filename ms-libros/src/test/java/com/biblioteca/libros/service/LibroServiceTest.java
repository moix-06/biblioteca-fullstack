package com.biblioteca.libros.service;

import com.biblioteca.libros.client.CatalogClient;
import com.biblioteca.libros.dto.LibroRequest;
import com.biblioteca.libros.dto.LibroResponse;
import com.biblioteca.libros.exception.BusinessRuleException;
import com.biblioteca.libros.exception.ResourceNotFoundException;
import com.biblioteca.libros.model.entity.Libro;
import com.biblioteca.libros.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Year;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private LibroService libroService;

    private LibroRequest buildValidRequest() {
        LibroRequest req = new LibroRequest();
        req.setTitulo("Cien anos de soledad");
        req.setIsbn("978-0307474728");
        req.setAnioPublicacion(1967);
        req.setIdioma("Espanol");
        req.setNumeroPaginas(471);
        req.setDescripcion("Novela clasica");
        req.setAutorId(1L);
        req.setEditorialId(1L);
        Set<Long> cats = new HashSet<>();
        cats.add(1L);
        req.setCategoriaIds(cats);
        return req;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("crearLibro")
    class CrearLibroTests {

        @Test
        @DisplayName("isbnDuplicado_lanzaExcepcion")
        void isbnDuplicado_lanzaExcepcion() {
            LibroRequest req = buildValidRequest();
            when(libroRepository.existsByIsbn(req.getIsbn())).thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> libroService.crear(req));
            verify(libroRepository, never()).save(any(Libro.class));
        }

        @Test
        @DisplayName("anioFuturo_lanzaExcepcion")
        void anioFuturo_lanzaExcepcion() {
            LibroRequest req = buildValidRequest();
            req.setAnioPublicacion(Year.now().getValue() + 1);
            when(libroRepository.existsByIsbn(req.getIsbn())).thenReturn(false);

            assertThrows(BusinessRuleException.class, () -> libroService.crear(req));
            verify(libroRepository, never()).save(any(Libro.class));
        }

        @Test
        @DisplayName("autorInexistente_lanzaExcepcion")
        void autorInexistente_lanzaExcepcion() {
            LibroRequest req = buildValidRequest();
            when(libroRepository.existsByIsbn(req.getIsbn())).thenReturn(false);
            doThrow(new ResourceNotFoundException("Autor no encontrado con id: 1"))
                    .when(catalogClient).validarAutor(req.getAutorId());

            assertThrows(ResourceNotFoundException.class, () -> libroService.crear(req));
            verify(libroRepository, never()).save(any(Libro.class));
        }

        @Test
        @DisplayName("editorialInexistente_lanzaExcepcion")
        void editorialInexistente_lanzaExcepcion() {
            LibroRequest req = buildValidRequest();
            when(libroRepository.existsByIsbn(req.getIsbn())).thenReturn(false);
            doThrow(new ResourceNotFoundException("Editorial no encontrada con id: 1"))
                    .when(catalogClient).validarEditorial(req.getEditorialId());

            assertThrows(ResourceNotFoundException.class, () -> libroService.crear(req));
            verify(libroRepository, never()).save(any(Libro.class));
        }

        @Test
        @DisplayName("categoriaInexistente_lanzaExcepcion")
        void categoriaInexistente_lanzaExcepcion() {
            LibroRequest req = buildValidRequest();
            when(libroRepository.existsByIsbn(req.getIsbn())).thenReturn(false);
            doThrow(new ResourceNotFoundException("Categoria no encontrada con id: 1"))
                    .when(catalogClient).validarCategoria(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> libroService.crear(req));
            verify(libroRepository, never()).save(any(Libro.class));
        }

        @Test
        @DisplayName("datosValidos_guardaLibro")
        void datosValidos_guardaLibro() {
            LibroRequest req = buildValidRequest();
            when(libroRepository.existsByIsbn(req.getIsbn())).thenReturn(false);
            when(libroRepository.save(any(Libro.class))).thenAnswer(inv -> {
                Libro l = inv.getArgument(0);
                l.setId(10L);
                return l;
            });

            LibroResponse resp = libroService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(req.getTitulo(), resp.getTitulo());
            assertEquals(req.getIsbn(), resp.getIsbn());
            verify(libroRepository).save(any(Libro.class));
            verify(catalogClient).validarAutor(req.getAutorId());
            verify(catalogClient).validarEditorial(req.getEditorialId());
            verify(catalogClient).validarCategoria(anyLong());
        }
    }

    @Nested
    @DisplayName("obtenerLibro")
    class ObtenerLibroTests {

        @Test
        @DisplayName("existente_retornaLibro")
        void existente_retornaLibro() {
            Libro libro = new Libro();
            libro.setId(1L);
            libro.setTitulo("Test");
            libro.setIsbn("111");
            when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

            LibroResponse resp = libroService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals("Test", resp.getTitulo());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(libroRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> libroService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("actualizarLibro")
    class ActualizarLibroTests {

        @Test
        @DisplayName("existente_actualiza")
        void existente_actualiza() {
            Libro libro = new Libro();
            libro.setId(1L);
            libro.setTitulo("Viejo");
            libro.setIsbn("111");
            when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
            when(libroRepository.save(any(Libro.class))).thenAnswer(inv -> inv.getArgument(0));

            LibroRequest req = buildValidRequest();

            LibroResponse resp = libroService.actualizar(1L, req);

            assertEquals(req.getTitulo(), resp.getTitulo());
            verify(libroRepository).save(any(Libro.class));
        }

        @Test
        @DisplayName("isbnDuplicadoEnOtro_lanzaExcepcion")
        void isbnDuplicadoEnOtro_lanzaExcepcion() {
            Libro libro = new Libro();
            libro.setId(1L);
            libro.setTitulo("Viejo");
            libro.setIsbn("111");
            when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

            Libro otro = new Libro();
            otro.setId(2L);
            otro.setIsbn("222");
            when(libroRepository.findByIsbn("222")).thenReturn(Optional.of(otro));

            LibroRequest req = buildValidRequest();
            req.setIsbn("222");

            assertThrows(BusinessRuleException.class, () -> libroService.actualizar(1L, req));
            verify(libroRepository, never()).save(any(Libro.class));
        }

        @Test
        @DisplayName("isbnMismoLibro_noLanzaExcepcion")
        void isbnMismoLibro_noLanzaExcepcion() {
            Libro libro = new Libro();
            libro.setId(1L);
            libro.setTitulo("Viejo");
            libro.setIsbn("111");
            when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
            when(libroRepository.save(any(Libro.class))).thenAnswer(inv -> inv.getArgument(0));

            LibroRequest req = buildValidRequest();
            req.setIsbn("111");

            LibroResponse resp = libroService.actualizar(1L, req);

            assertNotNull(resp);
            verify(libroRepository).save(any(Libro.class));
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(libroRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> libroService.actualizar(99L, buildValidRequest()));
        }
    }

    @Nested
    @DisplayName("eliminarLibro")
    class EliminarLibroTests {

        @Test
        @DisplayName("existente_elimina")
        void existente_elimina() {
            when(libroRepository.existsById(1L)).thenReturn(true);

            libroService.eliminar(1L);

            verify(libroRepository).deleteById(1L);
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(libroRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> libroService.eliminar(99L));
            verify(libroRepository, never()).deleteById(anyLong());
        }
    }

    @Test
    @DisplayName("listarTodos_retornaLista")
    void listarTodos_retornaLista() {
        Libro l1 = new Libro();
        l1.setId(1L);
        l1.setTitulo("A");
        Libro l2 = new Libro();
        l2.setId(2L);
        l2.setTitulo("B");
        when(libroRepository.findAll()).thenReturn(java.util.Arrays.asList(l1, l2));

        var result = libroService.listarTodos();

        assertEquals(2, result.size());
    }
}