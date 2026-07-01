package com.biblioteca.prestamos.service;

import com.biblioteca.prestamos.client.EjemplarRemoto;
import com.biblioteca.prestamos.client.PrestamosCatalogClient;
import com.biblioteca.prestamos.client.UsuarioRemoto;
import com.biblioteca.prestamos.dto.PrestamoRequest;
import com.biblioteca.prestamos.dto.PrestamoResponse;
import com.biblioteca.prestamos.exception.BusinessRuleException;
import com.biblioteca.prestamos.exception.ResourceNotFoundException;
import com.biblioteca.prestamos.model.entity.EstadoPrestamo;
import com.biblioteca.prestamos.model.entity.Prestamo;
import com.biblioteca.prestamos.repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private PrestamosCatalogClient catalogClient;

    private PrestamoService prestamoService;

    private static final int DURACION_DIAS = 14;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        prestamoService = new PrestamoService(prestamoRepository, catalogClient, DURACION_DIAS);
    }

    private PrestamoRequest buildValidRequest() {
        PrestamoRequest req = new PrestamoRequest();
        req.setUsuarioId(1L);
        req.setEjemplarId(1L);
        req.setObservaciones("Solicitud de prueba");
        return req;
    }

    private UsuarioRemoto buildUsuario(boolean bloqueado) {
        UsuarioRemoto u = new UsuarioRemoto();
        u.setId(1L);
        u.setNombre("Juan");
        u.setEmail("juan@example.cl");
        u.setRol("ALUMNO");
        u.setBloqueado(bloqueado);
        return u;
    }

    private EjemplarRemoto buildEjemplar(String estado) {
        EjemplarRemoto e = new EjemplarRemoto();
        e.setId(1L);
        e.setCodigo("EJ-001");
        e.setLibroId(1L);
        e.setSucursalId(1L);
        e.setEstado(estado);
        return e;
    }

    private Prestamo buildPrestamo(Long id, EstadoPrestamo estado, LocalDateTime fechaPrevista) {
        Prestamo p = new Prestamo();
        p.setId(id);
        p.setUsuarioId(1L);
        p.setEjemplarId(1L);
        p.setFechaPrestamo(LocalDateTime.now());
        p.setFechaDevolucionPrevista(fechaPrevista);
        p.setEstado(estado);
        return p;
    }

    @Nested
    @DisplayName("crearPrestamo")
    class CrearPrestamoTests {

        @Test
        @DisplayName("usuarioBloqueado_lanzaExcepcion")
        void usuarioBloqueado_lanzaExcepcion() {
            PrestamoRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(true));

            assertThrows(BusinessRuleException.class, () -> prestamoService.crear(req));
            verify(prestamoRepository, never()).save(any(Prestamo.class));
            verify(catalogClient, never()).cambiarEstadoEjemplar(anyLong(), anyString());
        }

        @Test
        @DisplayName("limiteAlcanzado_lanzaExcepcion")
        void limiteAlcanzado_lanzaExcepcion() {
            PrestamoRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLimiteUsuario(1L)).thenReturn(2);
            when(prestamoRepository.countByUsuarioIdAndEstadoIn(eq(1L), any()))
                    .thenReturn(2L);

            assertThrows(BusinessRuleException.class, () -> prestamoService.crear(req));
            verify(prestamoRepository, never()).save(any(Prestamo.class));
        }

        @Test
        @DisplayName("ejemplarNoDisponible_lanzaExcepcion")
        void ejemplarNoDisponible_lanzaExcepcion() {
            PrestamoRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLimiteUsuario(1L)).thenReturn(3);
            when(prestamoRepository.countByUsuarioIdAndEstadoIn(eq(1L), any())).thenReturn(0L);
            when(catalogClient.obtenerEjemplar(1L)).thenReturn(buildEjemplar("PRESTADO"));

            assertThrows(BusinessRuleException.class, () -> prestamoService.crear(req));
            verify(prestamoRepository, never()).save(any(Prestamo.class));
        }

        @Test
        @DisplayName("prestamoDuplicadoActivo_lanzaExcepcion")
        void prestamoDuplicadoActivo_lanzaExcepcion() {
            PrestamoRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLimiteUsuario(1L)).thenReturn(3);
            when(prestamoRepository.countByUsuarioIdAndEstadoIn(eq(1L), any())).thenReturn(0L);
            when(catalogClient.obtenerEjemplar(1L)).thenReturn(buildEjemplar("DISPONIBLE"));
            when(prestamoRepository.existsByUsuarioIdAndEjemplarIdAndEstado(1L, 1L, EstadoPrestamo.ACTIVO))
                    .thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> prestamoService.crear(req));
            verify(prestamoRepository, never()).save(any(Prestamo.class));
        }

        @Test
        @DisplayName("datosValidos_guardaPrestamoYMarcaEjemplar")
        void datosValidos_guardaPrestamoYMarcaEjemplar() {
            PrestamoRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLimiteUsuario(1L)).thenReturn(3);
            when(prestamoRepository.countByUsuarioIdAndEstadoIn(eq(1L), any())).thenReturn(0L);
            when(catalogClient.obtenerEjemplar(1L)).thenReturn(buildEjemplar("DISPONIBLE"));
            when(prestamoRepository.existsByUsuarioIdAndEjemplarIdAndEstado(
                    eq(1L), eq(1L), eq(EstadoPrestamo.ACTIVO))).thenReturn(false);
            when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> {
                Prestamo p = inv.getArgument(0);
                p.setId(10L);
                return p;
            });

            PrestamoResponse resp = prestamoService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(EstadoPrestamo.ACTIVO, resp.getEstado());
            assertEquals(req.getUsuarioId(), resp.getUsuarioId());
            assertEquals(req.getEjemplarId(), resp.getEjemplarId());
            assertNotNull(resp.getFechaDevolucionPrevista());
            verify(prestamoRepository).save(any(Prestamo.class));
            verify(catalogClient).cambiarEstadoEjemplar(1L, "PRESTADO");
        }

        @Test
        @DisplayName("fallaEnCambioDeEstado_haceRollbackYPropagaExcepcion")
        void fallaEnCambioDeEstado_haceRollbackYPropagaExcepcion() {
            PrestamoRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLimiteUsuario(1L)).thenReturn(3);
            when(prestamoRepository.countByUsuarioIdAndEstadoIn(eq(1L), any())).thenReturn(0L);
            when(catalogClient.obtenerEjemplar(1L)).thenReturn(buildEjemplar("DISPONIBLE"));
            when(prestamoRepository.existsByUsuarioIdAndEjemplarIdAndEstado(
                    eq(1L), eq(1L), eq(EstadoPrestamo.ACTIVO))).thenReturn(false);

            Prestamo guardado = new Prestamo();
            guardado.setId(10L);
            guardado.setUsuarioId(1L);
            guardado.setEjemplarId(1L);
            guardado.setEstado(EstadoPrestamo.ACTIVO);
            when(prestamoRepository.save(any(Prestamo.class))).thenReturn(guardado);
            doThrow(new RuntimeException("Servicio remoto caido"))
                    .when(catalogClient).cambiarEstadoEjemplar(1L, "PRESTADO");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> prestamoService.crear(req));
            assertEquals("Servicio remoto caido", ex.getMessage());
            verify(prestamoRepository).delete(guardado);
        }
    }

    @Nested
    @DisplayName("devolverPrestamo")
    class DevolverPrestamoTests {

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(prestamoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> prestamoService.devolver(99L));
        }

        @Test
        @DisplayName("yaDevuelto_lanzaExcepcion")
        void yaDevuelto_lanzaExcepcion() {
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.DEVUELTO, LocalDateTime.now().plusDays(1));
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));

            assertThrows(BusinessRuleException.class, () -> prestamoService.devolver(1L));
            verify(catalogClient, never()).cambiarEstadoEjemplar(anyLong(), anyString());
            verify(prestamoRepository, never()).save(any(Prestamo.class));
        }

        @Test
        @DisplayName("yaCancelado_lanzaExcepcion")
        void yaCancelado_lanzaExcepcion() {
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.CANCELADO, LocalDateTime.now().plusDays(1));
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));

            assertThrows(BusinessRuleException.class, () -> prestamoService.devolver(1L));
        }

        @Test
        @DisplayName("devolucionPuntual_marcaDevueltoYLiberaEjemplar")
        void devolucionPuntual_marcaDevueltoYLiberaEjemplar() {
            LocalDateTime prevista = LocalDateTime.now().plusDays(2);
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.ACTIVO, prevista);
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));
            when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));

            PrestamoResponse resp = prestamoService.devolver(1L);

            assertEquals(EstadoPrestamo.DEVUELTO, resp.getEstado());
            assertNotNull(resp.getFechaDevolucionReal());
            assertFalse(resp.isVencido());
            verify(catalogClient).cambiarEstadoEjemplar(1L, "DISPONIBLE");
            verify(prestamoRepository).save(any(Prestamo.class));
        }

        @Test
        @DisplayName("devolucionAtrasada_marcaVencidoYLiberaEjemplar")
        void devolucionAtrasada_marcaVencidoYLiberaEjemplar() {
            LocalDateTime prevista = LocalDateTime.now().minusDays(2);
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.ACTIVO, prevista);
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));
            when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));

            PrestamoResponse resp = prestamoService.devolver(1L);

            assertEquals(EstadoPrestamo.VENCIDO, resp.getEstado());
            assertTrue(resp.isVencido());
            verify(catalogClient).cambiarEstadoEjemplar(1L, "DISPONIBLE");
        }
    }

    @Nested
    @DisplayName("cancelarPrestamo")
    class CancelarPrestamoTests {

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(prestamoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> prestamoService.cancelar(99L));
        }

        @Test
        @DisplayName("yaDevuelto_lanzaExcepcion")
        void yaDevuelto_lanzaExcepcion() {
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.DEVUELTO, LocalDateTime.now().plusDays(1));
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));

            assertThrows(BusinessRuleException.class, () -> prestamoService.cancelar(1L));
            verify(catalogClient, never()).cambiarEstadoEjemplar(anyLong(), anyString());
        }

        @Test
        @DisplayName("yaCancelado_lanzaExcepcion")
        void yaCancelado_lanzaExcepcion() {
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.CANCELADO, LocalDateTime.now().plusDays(1));
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));

            assertThrows(BusinessRuleException.class, () -> prestamoService.cancelar(1L));
        }

        @Test
        @DisplayName("activo_marcaCanceladoYLiberaEjemplar")
        void activo_marcaCanceladoYLiberaEjemplar() {
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.ACTIVO, LocalDateTime.now().plusDays(2));
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));
            when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));

            PrestamoResponse resp = prestamoService.cancelar(1L);

            assertEquals(EstadoPrestamo.CANCELADO, resp.getEstado());
            assertNotNull(resp.getFechaDevolucionReal());
            verify(catalogClient).cambiarEstadoEjemplar(1L, "DISPONIBLE");
            verify(prestamoRepository).save(any(Prestamo.class));
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorIdTests {

        @Test
        @DisplayName("existente_retornaConVencidoFalse_siFechaPrevistaFutura")
        void existente_retornaConVencidoFalse_siFechaPrevistaFutura() {
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.ACTIVO, LocalDateTime.now().plusDays(3));
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));

            PrestamoResponse resp = prestamoService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertFalse(resp.isVencido());
        }

        @Test
        @DisplayName("existente_retornaConVencidoTrue_siFechaPrevistaPasada")
        void existente_retornaConVencidoTrue_siFechaPrevistaPasada() {
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.ACTIVO, LocalDateTime.now().minusDays(1));
            when(prestamoRepository.findById(1L)).thenReturn(Optional.of(p));

            PrestamoResponse resp = prestamoService.obtenerPorId(1L);

            assertTrue(resp.isVencido());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(prestamoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> prestamoService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("listarYBuscar")
    class ListarYBuscarTests {

        @Test
        @DisplayName("listarTodos_retornaLista")
        void listarTodos_retornaLista() {
            Prestamo p1 = buildPrestamo(1L, EstadoPrestamo.ACTIVO, LocalDateTime.now().plusDays(1));
            Prestamo p2 = buildPrestamo(2L, EstadoPrestamo.DEVUELTO, LocalDateTime.now().plusDays(1));
            when(prestamoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

            List<PrestamoResponse> result = prestamoService.listarTodos();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("buscarConFiltros_delegaAlRepositorio")
        void buscarConFiltros_delegaAlRepositorio() {
            Prestamo p = buildPrestamo(1L, EstadoPrestamo.ACTIVO, LocalDateTime.now().plusDays(1));
            when(prestamoRepository.buscar(1L, EstadoPrestamo.ACTIVO)).thenReturn(List.of(p));

            List<PrestamoResponse> result = prestamoService.buscar(1L, EstadoPrestamo.ACTIVO);

            assertEquals(1, result.size());
            verify(prestamoRepository).buscar(1L, EstadoPrestamo.ACTIVO);
        }

        @Test
        @DisplayName("buscarSinFiltros_retornaTodos")
        void buscarSinFiltros_retornaTodos() {
            Prestamo p1 = buildPrestamo(1L, EstadoPrestamo.ACTIVO, LocalDateTime.now().plusDays(1));
            Prestamo p2 = buildPrestamo(2L, EstadoPrestamo.DEVUELTO, LocalDateTime.now().plusDays(1));
            when(prestamoRepository.buscar(null, null)).thenReturn(Arrays.asList(p1, p2));

            List<PrestamoResponse> result = prestamoService.buscar(null, null);

            assertEquals(2, result.size());
            verify(prestamoRepository).buscar(null, null);
        }
    }
}
