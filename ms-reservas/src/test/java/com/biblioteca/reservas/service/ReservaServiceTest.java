package com.biblioteca.reservas.service;

import com.biblioteca.reservas.client.LibroRemoto;
import com.biblioteca.reservas.client.ReservasCatalogClient;
import com.biblioteca.reservas.client.UsuarioRemoto;
import com.biblioteca.reservas.dto.ReservaRequest;
import com.biblioteca.reservas.dto.ReservaResponse;
import com.biblioteca.reservas.exception.BusinessRuleException;
import com.biblioteca.reservas.exception.ResourceNotFoundException;
import com.biblioteca.reservas.model.entity.EstadoReserva;
import com.biblioteca.reservas.model.entity.Reserva;
import com.biblioteca.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ReservasCatalogClient catalogClient;

    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reservaService = new ReservaService(reservaRepository, catalogClient);
    }

    private ReservaRequest buildValidRequest() {
        return new ReservaRequest(1L, 1L);
    }

    private UsuarioRemoto buildUsuario(boolean bloqueado) {
        UsuarioRemoto u = new UsuarioRemoto();
        u.setId(1L);
        u.setNombre("Juan");
        u.setRol("ALUMNO");
        u.setBloqueado(bloqueado);
        return u;
    }

    private LibroRemoto buildLibro() {
        LibroRemoto l = new LibroRemoto();
        l.setId(1L);
        l.setTitulo("Cien anos de soledad");
        return l;
    }

    private Reserva buildReserva(Long id, EstadoReserva estado) {
        Reserva r = new Reserva();
        r.setId(id);
        r.setUsuarioId(1L);
        r.setLibroId(1L);
        r.setEstado(estado);
        return r;
    }

    @Nested
    @DisplayName("crearReserva")
    class CrearReservaTests {

        @Test
        @DisplayName("usuarioBloqueado_lanzaExcepcion")
        void usuarioBloqueado_lanzaExcepcion() {
            ReservaRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(true));

            assertThrows(BusinessRuleException.class, () -> reservaService.crear(req));
            verify(reservaRepository, never()).save(any(Reserva.class));
        }

        @Test
        @DisplayName("libroInexistente_lanzaExcepcion")
        void libroInexistente_lanzaExcepcion() {
            ReservaRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLibro(1L))
                    .thenThrow(new ResourceNotFoundException("Libro no encontrado con id: 1"));

            assertThrows(ResourceNotFoundException.class, () -> reservaService.crear(req));
            verify(reservaRepository, never()).save(any(Reserva.class));
            verify(catalogClient, never()).contarEjemplaresDisponibles(anyLong());
        }

        @Test
        @DisplayName("libroConEjemplaresDisponibles_lanzaExcepcion")
        void libroConEjemplaresDisponibles_lanzaExcepcion() {
            ReservaRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLibro(1L)).thenReturn(buildLibro());
            when(catalogClient.contarEjemplaresDisponibles(1L)).thenReturn(3);

            assertThrows(BusinessRuleException.class, () -> reservaService.crear(req));
            verify(reservaRepository, never()).save(any(Reserva.class));
        }

        @Test
        @DisplayName("reservaDuplicadaPendiente_lanzaExcepcion")
        void reservaDuplicadaPendiente_lanzaExcepcion() {
            ReservaRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLibro(1L)).thenReturn(buildLibro());
            when(catalogClient.contarEjemplaresDisponibles(1L)).thenReturn(0);
            when(reservaRepository.existsByUsuarioIdAndLibroIdAndEstado(1L, 1L, EstadoReserva.PENDIENTE))
                    .thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> reservaService.crear(req));
            verify(reservaRepository, never()).save(any(Reserva.class));
        }

        @Test
        @DisplayName("datosValidos_guardaReservaPendiente")
        void datosValidos_guardaReservaPendiente() {
            ReservaRequest req = buildValidRequest();
            when(catalogClient.obtenerUsuario(1L)).thenReturn(buildUsuario(false));
            when(catalogClient.obtenerLibro(1L)).thenReturn(buildLibro());
            when(catalogClient.contarEjemplaresDisponibles(1L)).thenReturn(0);
            when(reservaRepository.existsByUsuarioIdAndLibroIdAndEstado(1L, 1L, EstadoReserva.PENDIENTE))
                    .thenReturn(false);
            when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> {
                Reserva r = inv.getArgument(0);
                r.setId(10L);
                return r;
            });

            ReservaResponse resp = reservaService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(EstadoReserva.PENDIENTE, resp.getEstado());
            assertEquals(req.getUsuarioId(), resp.getUsuarioId());
            assertEquals(req.getLibroId(), resp.getLibroId());
            assertNotNull(resp.getFechaCreacion());
            verify(reservaRepository).save(any(Reserva.class));
        }
    }

    @Nested
    @DisplayName("cancelarReserva")
    class CancelarReservaTests {

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> reservaService.cancelar(99L));
        }

        @Test
        @DisplayName("yaCumplida_lanzaExcepcion")
        void yaCumplida_lanzaExcepcion() {
            Reserva r = buildReserva(1L, EstadoReserva.CUMPLIDA);
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

            assertThrows(BusinessRuleException.class, () -> reservaService.cancelar(1L));
            verify(reservaRepository, never()).save(any(Reserva.class));
        }

        @Test
        @DisplayName("yaCancelada_lanzaExcepcion")
        void yaCancelada_lanzaExcepcion() {
            Reserva r = buildReserva(1L, EstadoReserva.CANCELADA);
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

            assertThrows(BusinessRuleException.class, () -> reservaService.cancelar(1L));
            verify(reservaRepository, never()).save(any(Reserva.class));
        }

        @Test
        @DisplayName("pendiente_marcaCancelada")
        void pendiente_marcaCancelada() {
            Reserva r = buildReserva(1L, EstadoReserva.PENDIENTE);
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
            when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

            ReservaResponse resp = reservaService.cancelar(1L);

            assertEquals(EstadoReserva.CANCELADA, resp.getEstado());
            assertNotNull(resp.getFechaCancelada());
            verify(reservaRepository).save(any(Reserva.class));
        }
    }

    @Nested
    @DisplayName("cumplirReserva")
    class CumplirReservaTests {

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> reservaService.cumplir(99L));
        }

        @Test
        @DisplayName("yaCancelada_lanzaExcepcion")
        void yaCancelada_lanzaExcepcion() {
            Reserva r = buildReserva(1L, EstadoReserva.CANCELADA);
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

            assertThrows(BusinessRuleException.class, () -> reservaService.cumplir(1L));
            verify(reservaRepository, never()).save(any(Reserva.class));
        }

        @Test
        @DisplayName("yaCumplida_lanzaExcepcion")
        void yaCumplida_lanzaExcepcion() {
            Reserva r = buildReserva(1L, EstadoReserva.CUMPLIDA);
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

            assertThrows(BusinessRuleException.class, () -> reservaService.cumplir(1L));
            verify(reservaRepository, never()).save(any(Reserva.class));
        }

        @Test
        @DisplayName("pendiente_marcaCumplida")
        void pendiente_marcaCumplida() {
            Reserva r = buildReserva(1L, EstadoReserva.PENDIENTE);
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
            when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

            ReservaResponse resp = reservaService.cumplir(1L);

            assertEquals(EstadoReserva.CUMPLIDA, resp.getEstado());
            assertNotNull(resp.getFechaCumplida());
            verify(reservaRepository).save(any(Reserva.class));
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorIdTests {

        @Test
        @DisplayName("existente_retornaReserva")
        void existente_retornaReserva() {
            Reserva r = buildReserva(1L, EstadoReserva.PENDIENTE);
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

            ReservaResponse resp = reservaService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals(EstadoReserva.PENDIENTE, resp.getEstado());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> reservaService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("listarYBuscar")
    class ListarYBuscarTests {

        @Test
        @DisplayName("listarTodos_retornaLista")
        void listarTodos_retornaLista() {
            Reserva r1 = buildReserva(1L, EstadoReserva.PENDIENTE);
            Reserva r2 = buildReserva(2L, EstadoReserva.CANCELADA);
            when(reservaRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

            List<ReservaResponse> result = reservaService.listarTodos();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("buscarConFiltros_delegaAlRepositorio")
        void buscarConFiltros_delegaAlRepositorio() {
            Reserva r = buildReserva(1L, EstadoReserva.PENDIENTE);
            when(reservaRepository.buscar(1L, 1L, EstadoReserva.PENDIENTE)).thenReturn(List.of(r));

            List<ReservaResponse> result = reservaService.buscar(1L, 1L, EstadoReserva.PENDIENTE);

            assertEquals(1, result.size());
            verify(reservaRepository).buscar(1L, 1L, EstadoReserva.PENDIENTE);
        }

        @Test
        @DisplayName("buscarSinFiltros_retornaTodos")
        void buscarSinFiltros_retornaTodos() {
            Reserva r1 = buildReserva(1L, EstadoReserva.PENDIENTE);
            Reserva r2 = buildReserva(2L, EstadoReserva.CUMPLIDA);
            when(reservaRepository.buscar(null, null, null)).thenReturn(Arrays.asList(r1, r2));

            List<ReservaResponse> result = reservaService.buscar(null, null, null);

            assertEquals(2, result.size());
            verify(reservaRepository).buscar(null, null, null);
        }
    }
}
