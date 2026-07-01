package com.biblioteca.multas.service;

import com.biblioteca.multas.dto.MultaRequest;
import com.biblioteca.multas.dto.MultaResponse;
import com.biblioteca.multas.exception.BusinessRuleException;
import com.biblioteca.multas.exception.ResourceNotFoundException;
import com.biblioteca.multas.model.entity.EstadoMulta;
import com.biblioteca.multas.model.entity.Multa;
import com.biblioteca.multas.repository.MultaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    private MultaService multaService;

    private static final BigDecimal MONTO_POR_DIA = new BigDecimal("1000");
    private static final BigDecimal MONTO_MAXIMO = new BigDecimal("50000");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        multaService = new MultaService(multaRepository, MONTO_POR_DIA, MONTO_MAXIMO);
    }

    private MultaRequest buildRequestConMonto(BigDecimal monto) {
        MultaRequest r = new MultaRequest();
        r.setUsuarioId(1L);
        r.setPrestamoId(1L);
        r.setMonto(monto);
        r.setMotivo("Atraso en devolucion");
        return r;
    }

    private MultaRequest buildRequestConDias(int dias) {
        MultaRequest r = new MultaRequest();
        r.setUsuarioId(1L);
        r.setPrestamoId(1L);
        r.setDiasAtraso(dias);
        r.setMotivo("Atraso en devolucion");
        return r;
    }

    private Multa buildMulta(Long id, EstadoMulta estado) {
        Multa m = new Multa();
        m.setId(id);
        m.setUsuarioId(1L);
        m.setPrestamoId(1L);
        m.setMonto(new BigDecimal("5000"));
        m.setEstado(estado);
        return m;
    }

    @Nested
    @DisplayName("crearMulta")
    class CrearMultaTests {

        @Test
        @DisplayName("sinMontoNiDias_lanzaExcepcion")
        void sinMontoNiDias_lanzaExcepcion() {
            MultaRequest req = new MultaRequest();
            req.setUsuarioId(1L);
            req.setPrestamoId(1L);

            assertThrows(BusinessRuleException.class, () -> multaService.crear(req));
            verify(multaRepository, never()).save(any(Multa.class));
        }

        @Test
        @DisplayName("multaDuplicadaPendiente_lanzaExcepcion")
        void multaDuplicadaPendiente_lanzaExcepcion() {
            MultaRequest req = buildRequestConMonto(new BigDecimal("3000"));
            when(multaRepository.existsByPrestamoIdAndEstado(1L, EstadoMulta.PENDIENTE))
                    .thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> multaService.crear(req));
            verify(multaRepository, never()).save(any(Multa.class));
        }

        @Test
        @DisplayName("conMontoDirecto_guardaMulta")
        void conMontoDirecto_guardaMulta() {
            MultaRequest req = buildRequestConMonto(new BigDecimal("3500"));
            when(multaRepository.existsByPrestamoIdAndEstado(1L, EstadoMulta.PENDIENTE))
                    .thenReturn(false);
            when(multaRepository.save(any(Multa.class))).thenAnswer(inv -> {
                Multa m = inv.getArgument(0);
                m.setId(10L);
                return m;
            });

            MultaResponse resp = multaService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(EstadoMulta.PENDIENTE, resp.getEstado());
            assertEquals(new BigDecimal("3500"), resp.getMonto());
            assertEquals(req.getUsuarioId(), resp.getUsuarioId());
            assertEquals(req.getPrestamoId(), resp.getPrestamoId());
            verify(multaRepository).save(any(Multa.class));
        }

        @Test
        @DisplayName("conDiasAtraso_calculaMontoYGuarda")
        void conDiasAtraso_calculaMontoYGuarda() {
            MultaRequest req = buildRequestConDias(7);
            when(multaRepository.existsByPrestamoIdAndEstado(1L, EstadoMulta.PENDIENTE))
                    .thenReturn(false);
            when(multaRepository.save(any(Multa.class))).thenAnswer(inv -> {
                Multa m = inv.getArgument(0);
                m.setId(10L);
                return m;
            });

            MultaResponse resp = multaService.crear(req);

            assertEquals(new BigDecimal("7000"), resp.getMonto());
            assertEquals(EstadoMulta.PENDIENTE, resp.getEstado());
            verify(multaRepository).save(any(Multa.class));
        }

        @Test
        @DisplayName("conDiasAtraso_capeaEnMontoMaximo")
        void conDiasAtraso_capeaEnMontoMaximo() {
            MultaRequest req = buildRequestConDias(100);
            when(multaRepository.existsByPrestamoIdAndEstado(1L, EstadoMulta.PENDIENTE))
                    .thenReturn(false);
            when(multaRepository.save(any(Multa.class))).thenAnswer(inv -> {
                Multa m = inv.getArgument(0);
                m.setId(10L);
                return m;
            });

            MultaResponse resp = multaService.crear(req);

            assertEquals(MONTO_MAXIMO, resp.getMonto());
        }

        @Test
        @DisplayName("conMontoSobreMaximo_capeaEnMontoMaximo")
        void conMontoSobreMaximo_capeaEnMontoMaximo() {
            MultaRequest req = buildRequestConMonto(new BigDecimal("99999"));
            when(multaRepository.existsByPrestamoIdAndEstado(1L, EstadoMulta.PENDIENTE))
                    .thenReturn(false);
            when(multaRepository.save(any(Multa.class))).thenAnswer(inv -> {
                Multa m = inv.getArgument(0);
                m.setId(10L);
                return m;
            });

            MultaResponse resp = multaService.crear(req);

            assertEquals(MONTO_MAXIMO, resp.getMonto());
        }
    }

    @Nested
    @DisplayName("pagarMulta")
    class PagarMultaTests {

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(multaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> multaService.pagar(99L));
        }

        @Test
        @DisplayName("yaPagada_lanzaExcepcion")
        void yaPagada_lanzaExcepcion() {
            Multa m = buildMulta(1L, EstadoMulta.PAGADA);
            when(multaRepository.findById(1L)).thenReturn(Optional.of(m));

            assertThrows(BusinessRuleException.class, () -> multaService.pagar(1L));
            verify(multaRepository, never()).save(any(Multa.class));
        }

        @Test
        @DisplayName("pendiente_marcaPagada")
        void pendiente_marcaPagada() {
            Multa m = buildMulta(1L, EstadoMulta.PENDIENTE);
            when(multaRepository.findById(1L)).thenReturn(Optional.of(m));
            when(multaRepository.save(any(Multa.class))).thenAnswer(inv -> inv.getArgument(0));

            MultaResponse resp = multaService.pagar(1L);

            assertEquals(EstadoMulta.PAGADA, resp.getEstado());
            assertNotNull(resp.getFechaPago());
            verify(multaRepository).save(any(Multa.class));
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorIdTests {

        @Test
        @DisplayName("existente_retornaMulta")
        void existente_retornaMulta() {
            Multa m = buildMulta(1L, EstadoMulta.PENDIENTE);
            when(multaRepository.findById(1L)).thenReturn(Optional.of(m));

            MultaResponse resp = multaService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals(EstadoMulta.PENDIENTE, resp.getEstado());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(multaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> multaService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("listarYBuscar")
    class ListarYBuscarTests {

        @Test
        @DisplayName("listarTodos_retornaLista")
        void listarTodos_retornaLista() {
            Multa m1 = buildMulta(1L, EstadoMulta.PENDIENTE);
            Multa m2 = buildMulta(2L, EstadoMulta.PAGADA);
            when(multaRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

            List<MultaResponse> result = multaService.listarTodos();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("buscarConFiltros_delegaAlRepositorio")
        void buscarConFiltros_delegaAlRepositorio() {
            Multa m = buildMulta(1L, EstadoMulta.PENDIENTE);
            when(multaRepository.buscar(1L, 1L, EstadoMulta.PENDIENTE)).thenReturn(List.of(m));

            List<MultaResponse> result = multaService.buscar(1L, 1L, EstadoMulta.PENDIENTE);

            assertEquals(1, result.size());
            verify(multaRepository).buscar(1L, 1L, EstadoMulta.PENDIENTE);
        }

        @Test
        @DisplayName("buscarSinFiltros_retornaTodos")
        void buscarSinFiltros_retornaTodos() {
            Multa m1 = buildMulta(1L, EstadoMulta.PENDIENTE);
            Multa m2 = buildMulta(2L, EstadoMulta.PAGADA);
            when(multaRepository.buscar(null, null, null)).thenReturn(Arrays.asList(m1, m2));

            List<MultaResponse> result = multaService.buscar(null, null, null);

            assertEquals(2, result.size());
            verify(multaRepository).buscar(null, null, null);
        }
    }
}
