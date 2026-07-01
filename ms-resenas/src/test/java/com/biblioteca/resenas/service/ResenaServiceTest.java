package com.biblioteca.resenas.service;

import com.biblioteca.resenas.dto.ResenaRequest;
import com.biblioteca.resenas.dto.ResenaResponse;
import com.biblioteca.resenas.exception.BusinessRuleException;
import com.biblioteca.resenas.exception.ResourceNotFoundException;
import com.biblioteca.resenas.model.entity.Resena;
import com.biblioteca.resenas.repository.ResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    private ResenaService resenaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resenaService = new ResenaService(resenaRepository);
    }

    private ResenaRequest buildValidRequest() {
        return new ResenaRequest(1L, 1L, 5, "Excelente libro");
    }

    private Resena buildResena(Long id) {
        Resena r = new Resena();
        r.setId(id);
        r.setUsuarioId(1L);
        r.setLibroId(1L);
        r.setCalificacion(4);
        r.setComentario("Bueno");
        return r;
    }

    @Nested
    @DisplayName("crearResena")
    class CrearResenaTests {

        @Test
        @DisplayName("duplicada_lanzaExcepcion")
        void duplicada_lanzaExcepcion() {
            ResenaRequest req = buildValidRequest();
            when(resenaRepository.existsByUsuarioIdAndLibroId(1L, 1L)).thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> resenaService.crear(req));
            verify(resenaRepository, never()).save(any(Resena.class));
        }

        @Test
        @DisplayName("datosValidos_guardaResena")
        void datosValidos_guardaResena() {
            ResenaRequest req = buildValidRequest();
            when(resenaRepository.existsByUsuarioIdAndLibroId(1L, 1L)).thenReturn(false);
            when(resenaRepository.save(any(Resena.class))).thenAnswer(inv -> {
                Resena r = inv.getArgument(0);
                r.setId(10L);
                return r;
            });

            ResenaResponse resp = resenaService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(1L, resp.getUsuarioId());
            assertEquals(1L, resp.getLibroId());
            assertEquals(5, resp.getCalificacion());
            assertEquals("Excelente libro", resp.getComentario());
            assertNotNull(resp.getFechaCreacion());
            assertNull(resp.getFechaEdicion());
            verify(resenaRepository).save(any(Resena.class));
        }
    }

    @Nested
    @DisplayName("actualizarResena")
    class ActualizarResenaTests {

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> resenaService.actualizar(99L, buildValidRequest()));
        }

        @Test
        @DisplayName("existente_actualizaYSetFechaEdicion")
        void existente_actualizaYSetFechaEdicion() {
            Resena r = buildResena(1L);
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(r));
            when(resenaRepository.save(any(Resena.class))).thenAnswer(inv -> inv.getArgument(0));

            ResenaRequest req = new ResenaRequest(1L, 1L, 3, "Cambie de opinion");

            ResenaResponse resp = resenaService.actualizar(1L, req);

            assertEquals(3, resp.getCalificacion());
            assertEquals("Cambie de opinion", resp.getComentario());
            assertNotNull(resp.getFechaEdicion());
            assertEquals(1L, resp.getUsuarioId());
            assertEquals(1L, resp.getLibroId());

            ArgumentCaptor<Resena> captor = ArgumentCaptor.forClass(Resena.class);
            verify(resenaRepository).save(captor.capture());
            assertEquals(1L, captor.getValue().getUsuarioId());
            assertEquals(1L, captor.getValue().getLibroId());
        }
    }

    @Nested
    @DisplayName("eliminarResena")
    class EliminarResenaTests {

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(resenaRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> resenaService.eliminar(99L));
            verify(resenaRepository, never()).deleteById(any(Long.class));
        }

        @Test
        @DisplayName("existente_elimina")
        void existente_elimina() {
            when(resenaRepository.existsById(1L)).thenReturn(true);

            resenaService.eliminar(1L);

            verify(resenaRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorIdTests {

        @Test
        @DisplayName("existente_retornaResena")
        void existente_retornaResena() {
            Resena r = buildResena(1L);
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(r));

            ResenaResponse resp = resenaService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals(4, resp.getCalificacion());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> resenaService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("listarYBuscar")
    class ListarYBuscarTests {

        @Test
        @DisplayName("listarTodos_retornaLista")
        void listarTodos_retornaLista() {
            Resena r1 = buildResena(1L);
            Resena r2 = buildResena(2L);
            when(resenaRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

            List<ResenaResponse> result = resenaService.listarTodos();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("buscarConFiltros_delegaAlRepositorio")
        void buscarConFiltros_delegaAlRepositorio() {
            Resena r = buildResena(1L);
            when(resenaRepository.buscar(1L, 1L, 5)).thenReturn(List.of(r));

            List<ResenaResponse> result = resenaService.buscar(1L, 1L, 5);

            assertEquals(1, result.size());
            verify(resenaRepository).buscar(1L, 1L, 5);
        }

        @Test
        @DisplayName("buscarSinFiltros_retornaTodos")
        void buscarSinFiltros_retornaTodos() {
            Resena r1 = buildResena(1L);
            Resena r2 = buildResena(2L);
            when(resenaRepository.buscar(null, null, null)).thenReturn(Arrays.asList(r1, r2));

            List<ResenaResponse> result = resenaService.buscar(null, null, null);

            assertEquals(2, result.size());
            verify(resenaRepository).buscar(null, null, null);
        }
    }
}
