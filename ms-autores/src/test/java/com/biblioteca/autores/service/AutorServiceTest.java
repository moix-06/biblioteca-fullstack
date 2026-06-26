package com.biblioteca.autores.service;

import com.biblioteca.autores.client.LibroLookupClient;
import com.biblioteca.autores.dto.AutorRequest;
import com.biblioteca.autores.dto.AutorResponse;
import com.biblioteca.autores.exception.BusinessRuleException;
import com.biblioteca.autores.exception.ResourceNotFoundException;
import com.biblioteca.autores.model.entity.Autor;
import com.biblioteca.autores.repository.AutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
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

class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private LibroLookupClient libroLookupClient;

    @InjectMocks
    private AutorService autorService;

    private AutorRequest buildValidRequest() {
        AutorRequest req = new AutorRequest();
        req.setNombre("Gabriel");
        req.setApellido("Garcia Marquez");
        req.setFechaNacimiento(LocalDate.of(1927, 3, 6));
        req.setNacionalidad("Colombiana");
        req.setBiografia("Autor de Cien anos de soledad");
        return req;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("crearAutor")
    class CrearAutorTests {

        @Test
        @DisplayName("datosValidos_guardaAutor")
        void datosValidos_guardaAutor() {
            AutorRequest req = buildValidRequest();
            when(autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase(req.getNombre(), req.getApellido()))
                    .thenReturn(Optional.empty());
            when(autorRepository.save(any(Autor.class))).thenAnswer(inv -> {
                Autor a = inv.getArgument(0);
                a.setId(10L);
                return a;
            });

            AutorResponse resp = autorService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(req.getNombre(), resp.getNombre());
            assertEquals(req.getApellido(), resp.getApellido());
            verify(autorRepository).save(any(Autor.class));
        }

        @Test
        @DisplayName("duplicadoCaseInsensitive_lanzaExcepcion")
        void duplicadoCaseInsensitive_lanzaExcepcion() {
            AutorRequest req = buildValidRequest();
            Autor existente = new Autor();
            existente.setId(99L);
            existente.setNombre("GABRIEL");
            existente.setApellido("garcia marquez");
            when(autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase(req.getNombre(), req.getApellido()))
                    .thenReturn(Optional.of(existente));

            assertThrows(BusinessRuleException.class, () -> autorService.crear(req));
            verify(autorRepository, never()).save(any(Autor.class));
        }

        @Test
        @DisplayName("fechaFutura_lanzaExcepcion")
        void fechaFutura_lanzaExcepcion() {
            AutorRequest req = buildValidRequest();
            req.setFechaNacimiento(LocalDate.now().plusDays(1));
            when(autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase(req.getNombre(), req.getApellido()))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessRuleException.class, () -> autorService.crear(req));
            verify(autorRepository, never()).save(any(Autor.class));
        }

        @Test
        @DisplayName("fechaAnteriorA1500_lanzaExcepcion")
        void fechaAnteriorA1500_lanzaExcepcion() {
            AutorRequest req = buildValidRequest();
            req.setFechaNacimiento(LocalDate.of(1400, 1, 1));
            when(autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase(req.getNombre(), req.getApellido()))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessRuleException.class, () -> autorService.crear(req));
            verify(autorRepository, never()).save(any(Autor.class));
        }

        @Test
        @DisplayName("fechaNula_noValida_yGuarda")
        void fechaNula_noValida_yGuarda() {
            AutorRequest req = buildValidRequest();
            req.setFechaNacimiento(null);
            when(autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase(req.getNombre(), req.getApellido()))
                    .thenReturn(Optional.empty());
            when(autorRepository.save(any(Autor.class))).thenAnswer(inv -> {
                Autor a = inv.getArgument(0);
                a.setId(1L);
                return a;
            });

            AutorResponse resp = autorService.crear(req);

            assertNotNull(resp.getId());
            verify(autorRepository).save(any(Autor.class));
        }
    }

    @Nested
    @DisplayName("obtenerAutor")
    class ObtenerAutorTests {

        @Test
        @DisplayName("existente_retornaAutor")
        void existente_retornaAutor() {
            Autor autor = new Autor();
            autor.setId(1L);
            autor.setNombre("Pablo");
            autor.setApellido("Neruda");
            when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

            AutorResponse resp = autorService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals("Pablo", resp.getNombre());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(autorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> autorService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("actualizarAutor")
    class ActualizarAutorTests {

        @Test
        @DisplayName("existente_actualiza")
        void existente_actualiza() {
            Autor autor = new Autor();
            autor.setId(1L);
            autor.setNombre("Viejo");
            autor.setApellido("Apellido");
            when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
            when(autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase(any(), any()))
                    .thenReturn(Optional.empty());
            when(autorRepository.save(any(Autor.class))).thenAnswer(inv -> inv.getArgument(0));

            AutorRequest req = buildValidRequest();
            AutorResponse resp = autorService.actualizar(1L, req);

            assertEquals(req.getNombre(), resp.getNombre());
            verify(autorRepository).save(any(Autor.class));
        }

        @Test
        @DisplayName("duplicadoEnOtroAutor_lanzaExcepcion")
        void duplicadoEnOtroAutor_lanzaExcepcion() {
            Autor autor = new Autor();
            autor.setId(1L);
            autor.setNombre("Viejo");
            autor.setApellido("Apellido");
            when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

            Autor otro = new Autor();
            otro.setId(2L);
            otro.setNombre("Gabriel");
            otro.setApellido("Garcia Marquez");
            when(autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase("Gabriel", "Garcia Marquez"))
                    .thenReturn(Optional.of(otro));

            AutorRequest req = buildValidRequest();

            assertThrows(BusinessRuleException.class, () -> autorService.actualizar(1L, req));
            verify(autorRepository, never()).save(any(Autor.class));
        }

        @Test
        @DisplayName("mismoNombreEnMismoAutor_noLanzaExcepcion")
        void mismoNombreEnMismoAutor_noLanzaExcepcion() {
            Autor autor = new Autor();
            autor.setId(1L);
            autor.setNombre("Gabriel");
            autor.setApellido("Garcia Marquez");
            when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
            when(autorRepository.findByNombreIgnoreCaseAndApellidoIgnoreCase("Gabriel", "Garcia Marquez"))
                    .thenReturn(Optional.of(autor));
            when(autorRepository.save(any(Autor.class))).thenAnswer(inv -> inv.getArgument(0));

            AutorRequest req = buildValidRequest();
            AutorResponse resp = autorService.actualizar(1L, req);

            assertNotNull(resp);
            verify(autorRepository).save(any(Autor.class));
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(autorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> autorService.actualizar(99L, buildValidRequest()));
        }
    }

    @Nested
    @DisplayName("eliminarAutor")
    class EliminarAutorTests {

        @Test
        @DisplayName("existenteSinLibros_elimina")
        void existenteSinLibros_elimina() {
            when(autorRepository.existsById(1L)).thenReturn(true);
            when(libroLookupClient.tieneLibros(1L)).thenReturn(false);

            autorService.eliminar(1L);

            verify(autorRepository).deleteById(1L);
        }

        @Test
        @DisplayName("existenteConLibros_lanzaExcepcion")
        void existenteConLibros_lanzaExcepcion() {
            when(autorRepository.existsById(1L)).thenReturn(true);
            when(libroLookupClient.tieneLibros(1L)).thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> autorService.eliminar(1L));
            verify(autorRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(autorRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> autorService.eliminar(99L));
            verify(autorRepository, never()).deleteById(anyLong());
        }
    }

    @Test
    @DisplayName("listarTodos_retornaLista")
    void listarTodos_retornaLista() {
        Autor a1 = new Autor();
        a1.setId(1L);
        a1.setNombre("A");
        Autor a2 = new Autor();
        a2.setId(2L);
        a2.setNombre("B");
        when(autorRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        var result = autorService.listarTodos();

        assertEquals(2, result.size());
    }
}