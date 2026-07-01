package com.biblioteca.ejemplares.service;

import com.biblioteca.ejemplares.client.EjemplarCatalogClient;
import com.biblioteca.ejemplares.dto.EjemplarRequest;
import com.biblioteca.ejemplares.dto.EjemplarResponse;
import com.biblioteca.ejemplares.exception.BusinessRuleException;
import com.biblioteca.ejemplares.exception.ResourceNotFoundException;
import com.biblioteca.ejemplares.model.entity.Ejemplar;
import com.biblioteca.ejemplares.model.entity.EstadoEjemplar;
import com.biblioteca.ejemplares.repository.EjemplarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EjemplarServiceTest {

    @Mock
    private EjemplarRepository ejemplarRepository;

    @Mock
    private EjemplarCatalogClient catalogClient;

    @InjectMocks
    private EjemplarService ejemplarService;

    private EjemplarRequest buildValidRequest() {
        EjemplarRequest req = new EjemplarRequest();
        req.setCodigo("EJ-001");
        req.setLibroId(1L);
        req.setSucursalId(1L);
        req.setEstado(EstadoEjemplar.DISPONIBLE);
        req.setUbicacion("Pasillo 3, Estante B");
        req.setObservaciones("Nuevo");
        return req;
    }

    private Ejemplar buildEjemplar(Long id, EstadoEjemplar estado) {
        Ejemplar e = new Ejemplar();
        e.setId(id);
        e.setCodigo("EJ-" + id);
        e.setLibroId(1L);
        e.setSucursalId(1L);
        e.setEstado(estado);
        return e;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("crearEjemplar")
    class CrearEjemplarTests {

        @Test
        @DisplayName("codigoDuplicado_lanzaExcepcion")
        void codigoDuplicado_lanzaExcepcion() {
            EjemplarRequest req = buildValidRequest();
            when(ejemplarRepository.existsByCodigo(req.getCodigo())).thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> ejemplarService.crear(req));
            verify(ejemplarRepository, never()).save(any(Ejemplar.class));
            verify(catalogClient, never()).validarLibro(anyLong());
        }

        @Test
        @DisplayName("libroInexistente_lanzaExcepcion")
        void libroInexistente_lanzaExcepcion() {
            EjemplarRequest req = buildValidRequest();
            when(ejemplarRepository.existsByCodigo(req.getCodigo())).thenReturn(false);
            doThrow(new ResourceNotFoundException("Libro no encontrado con id: 1"))
                    .when(catalogClient).validarLibro(req.getLibroId());

            assertThrows(ResourceNotFoundException.class, () -> ejemplarService.crear(req));
            verify(ejemplarRepository, never()).save(any(Ejemplar.class));
        }

        @Test
        @DisplayName("sucursalInexistente_lanzaExcepcion")
        void sucursalInexistente_lanzaExcepcion() {
            EjemplarRequest req = buildValidRequest();
            when(ejemplarRepository.existsByCodigo(req.getCodigo())).thenReturn(false);
            doThrow(new ResourceNotFoundException("Sucursal no encontrada con id: 1"))
                    .when(catalogClient).validarSucursal(req.getSucursalId());

            assertThrows(ResourceNotFoundException.class, () -> ejemplarService.crear(req));
            verify(ejemplarRepository, never()).save(any(Ejemplar.class));
        }

        @Test
        @DisplayName("datosValidos_guardaEjemplar")
        void datosValidos_guardaEjemplar() {
            EjemplarRequest req = buildValidRequest();
            when(ejemplarRepository.existsByCodigo(req.getCodigo())).thenReturn(false);
            when(ejemplarRepository.save(any(Ejemplar.class))).thenAnswer(inv -> {
                Ejemplar e = inv.getArgument(0);
                e.setId(10L);
                return e;
            });

            EjemplarResponse resp = ejemplarService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(req.getCodigo(), resp.getCodigo());
            assertEquals(req.getEstado(), resp.getEstado());
            verify(ejemplarRepository).save(any(Ejemplar.class));
            verify(catalogClient).validarLibro(req.getLibroId());
            verify(catalogClient).validarSucursal(req.getSucursalId());
        }
    }

    @Nested
    @DisplayName("obtenerEjemplar")
    class ObtenerEjemplarTests {

        @Test
        @DisplayName("existente_retornaEjemplar")
        void existente_retornaEjemplar() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));

            EjemplarResponse resp = ejemplarService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals("EJ-1", resp.getCodigo());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(ejemplarRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> ejemplarService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("actualizarEjemplar")
    class ActualizarEjemplarTests {

        @Test
        @DisplayName("existente_actualiza")
        void existente_actualiza() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));
            when(ejemplarRepository.save(any(Ejemplar.class))).thenAnswer(inv -> inv.getArgument(0));

            EjemplarRequest req = buildValidRequest();

            EjemplarResponse resp = ejemplarService.actualizar(1L, req);

            assertEquals(req.getCodigo(), resp.getCodigo());
            verify(ejemplarRepository).save(any(Ejemplar.class));
        }

        @Test
        @DisplayName("codigoDuplicadoEnOtro_lanzaExcepcion")
        void codigoDuplicadoEnOtro_lanzaExcepcion() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));

            Ejemplar otro = buildEjemplar(2L, EstadoEjemplar.DISPONIBLE);
            when(ejemplarRepository.findByCodigo("NUEVO-001")).thenReturn(Optional.of(otro));

            EjemplarRequest req = buildValidRequest();
            req.setCodigo("NUEVO-001");

            assertThrows(BusinessRuleException.class, () -> ejemplarService.actualizar(1L, req));
            verify(ejemplarRepository, never()).save(any(Ejemplar.class));
        }

        @Test
        @DisplayName("codigoMismoEjemplar_noLanzaExcepcion")
        void codigoMismoEjemplar_noLanzaExcepcion() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            e.setCodigo("EJ-MISMO");
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));
            when(ejemplarRepository.save(any(Ejemplar.class))).thenAnswer(inv -> inv.getArgument(0));

            EjemplarRequest req = buildValidRequest();
            req.setCodigo("EJ-MISMO");

            EjemplarResponse resp = ejemplarService.actualizar(1L, req);

            assertNotNull(resp);
            verify(ejemplarRepository).save(any(Ejemplar.class));
        }

        @Test
        @DisplayName("transicionInvalida_PrestadoABaja_lanzaExcepcion")
        void transicionInvalida_PrestadoABaja_lanzaExcepcion() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.PRESTADO);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));

            EjemplarRequest req = buildValidRequest();
            req.setEstado(EstadoEjemplar.BAJA);

            assertThrows(BusinessRuleException.class, () -> ejemplarService.actualizar(1L, req));
            verify(ejemplarRepository, never()).save(any(Ejemplar.class));
        }

        @Test
        @DisplayName("transicionValida_PrestadoADisponible_actualiza")
        void transicionValida_PrestadoADisponible_actualiza() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.PRESTADO);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));
            when(ejemplarRepository.save(any(Ejemplar.class))).thenAnswer(inv -> inv.getArgument(0));

            EjemplarRequest req = buildValidRequest();
            req.setEstado(EstadoEjemplar.DISPONIBLE);

            EjemplarResponse resp = ejemplarService.actualizar(1L, req);

            assertEquals(EstadoEjemplar.DISPONIBLE, resp.getEstado());
            verify(ejemplarRepository).save(any(Ejemplar.class));
        }

        @Test
        @DisplayName("transicionValida_MismoEstado_actualiza")
        void transicionValida_MismoEstado_actualiza() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));
            when(ejemplarRepository.save(any(Ejemplar.class))).thenAnswer(inv -> inv.getArgument(0));

            EjemplarRequest req = buildValidRequest();
            req.setEstado(EstadoEjemplar.DISPONIBLE);

            EjemplarResponse resp = ejemplarService.actualizar(1L, req);

            assertEquals(EstadoEjemplar.DISPONIBLE, resp.getEstado());
            verify(ejemplarRepository).save(any(Ejemplar.class));
        }

        @Test
        @DisplayName("libroInexistente_lanzaExcepcion")
        void libroInexistente_lanzaExcepcion() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));
            doThrow(new ResourceNotFoundException("Libro no encontrado con id: 1"))
                    .when(catalogClient).validarLibro(anyLong());

            assertThrows(ResourceNotFoundException.class, () -> ejemplarService.actualizar(1L, buildValidRequest()));
            verify(ejemplarRepository, never()).save(any(Ejemplar.class));
        }
    }

    @Nested
    @DisplayName("eliminarEjemplar")
    class EliminarEjemplarTests {

        @Test
        @DisplayName("disponible_elimina")
        void disponible_elimina() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));

            ejemplarService.eliminar(1L);

            verify(ejemplarRepository).delete(e);
        }

        @Test
        @DisplayName("prestado_lanzaExcepcion")
        void prestado_lanzaExcepcion() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.PRESTADO);
            when(ejemplarRepository.findById(1L)).thenReturn(Optional.of(e));

            assertThrows(BusinessRuleException.class, () -> ejemplarService.eliminar(1L));
            verify(ejemplarRepository, never()).delete(any(Ejemplar.class));
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(ejemplarRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> ejemplarService.eliminar(99L));
        }
    }

    @Nested
    @DisplayName("listarYBuscar")
    class ListarYBuscarTests {

        @Test
        @DisplayName("listarTodos_retornaLista")
        void listarTodos_retornaLista() {
            Ejemplar e1 = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            Ejemplar e2 = buildEjemplar(2L, EstadoEjemplar.PRESTADO);
            when(ejemplarRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

            List<EjemplarResponse> result = ejemplarService.listarTodos();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("buscarConFiltros_delegaAlRepositorio")
        void buscarConFiltros_delegaAlRepositorio() {
            Ejemplar e = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            when(ejemplarRepository.buscar(1L, null, null)).thenReturn(List.of(e));

            List<EjemplarResponse> result = ejemplarService.buscar(1L, null, null);

            assertEquals(1, result.size());
            verify(ejemplarRepository).buscar(1L, null, null);
        }

        @Test
        @DisplayName("buscarSinFiltros_retornaTodos")
        void buscarSinFiltros_retornaTodos() {
            Ejemplar e1 = buildEjemplar(1L, EstadoEjemplar.DISPONIBLE);
            Ejemplar e2 = buildEjemplar(2L, EstadoEjemplar.PRESTADO);
            when(ejemplarRepository.buscar(null, null, null)).thenReturn(Arrays.asList(e1, e2));

            List<EjemplarResponse> result = ejemplarService.buscar(null, null, null);

            assertEquals(2, result.size());
            verify(ejemplarRepository).buscar(eq(null), eq(null), eq(null));
        }
    }
}
