package com.biblioteca.sucursales.service;

import com.biblioteca.sucursales.dto.SucursalRequest;
import com.biblioteca.sucursales.dto.SucursalResponse;
import com.biblioteca.sucursales.exception.BusinessRuleException;
import com.biblioteca.sucursales.exception.ResourceNotFoundException;
import com.biblioteca.sucursales.model.entity.Sucursal;
import com.biblioteca.sucursales.repository.SucursalRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    private SucursalRequest buildValidRequest() {
        SucursalRequest req = new SucursalRequest();
        req.setNombre("Biblioteca Central");
        req.setDireccion("Av. Principal 123");
        req.setCiudad("Santiago");
        req.setComuna("Santiago Centro");
        req.setTelefono("+56 2 2345 6789");
        req.setEmail("central@biblioteca.cl");
        return req;
    }

    private Sucursal buildSucursal(Long id, String nombre) {
        Sucursal s = new Sucursal();
        s.setId(id);
        s.setNombre(nombre);
        s.setDireccion("Av. Principal 123");
        s.setCiudad("Santiago");
        return s;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("crearSucursal")
    class CrearSucursalTests {

        @Test
        @DisplayName("nombreDuplicado_lanzaExcepcion")
        void nombreDuplicado_lanzaExcepcion() {
            SucursalRequest req = buildValidRequest();
            when(sucursalRepository.findByNombreIgnoreCase(req.getNombre()))
                    .thenReturn(Optional.of(buildSucursal(1L, req.getNombre())));

            assertThrows(BusinessRuleException.class, () -> sucursalService.crear(req));
            verify(sucursalRepository, never()).save(any(Sucursal.class));
        }

        @Test
        @DisplayName("datosValidos_guardaSucursal")
        void datosValidos_guardaSucursal() {
            SucursalRequest req = buildValidRequest();
            when(sucursalRepository.findByNombreIgnoreCase(req.getNombre())).thenReturn(Optional.empty());
            when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(inv -> {
                Sucursal s = inv.getArgument(0);
                s.setId(10L);
                return s;
            });

            SucursalResponse resp = sucursalService.crear(req);

            assertNotNull(resp.getId());
            assertEquals(req.getNombre(), resp.getNombre());
            assertEquals(req.getDireccion(), resp.getDireccion());
            assertEquals(req.getCiudad(), resp.getCiudad());
            verify(sucursalRepository).save(any(Sucursal.class));
        }
    }

    @Nested
    @DisplayName("obtenerSucursal")
    class ObtenerSucursalTests {

        @Test
        @DisplayName("existente_retornaSucursal")
        void existente_retornaSucursal() {
            Sucursal s = buildSucursal(1L, "Biblioteca Central");
            when(sucursalRepository.findById(1L)).thenReturn(Optional.of(s));

            SucursalResponse resp = sucursalService.obtenerPorId(1L);

            assertEquals(1L, resp.getId());
            assertEquals("Biblioteca Central", resp.getNombre());
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> sucursalService.obtenerPorId(99L));
        }
    }

    @Nested
    @DisplayName("actualizarSucursal")
    class ActualizarSucursalTests {

        @Test
        @DisplayName("existente_actualiza")
        void existente_actualiza() {
            Sucursal s = buildSucursal(1L, "Viejo");
            when(sucursalRepository.findById(1L)).thenReturn(Optional.of(s));
            when(sucursalRepository.findByNombreIgnoreCase("Biblioteca Central")).thenReturn(Optional.empty());
            when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(inv -> inv.getArgument(0));

            SucursalRequest req = buildValidRequest();

            SucursalResponse resp = sucursalService.actualizar(1L, req);

            assertEquals(req.getNombre(), resp.getNombre());
            verify(sucursalRepository).save(any(Sucursal.class));
        }

        @Test
        @DisplayName("nombreDuplicadoEnOtra_lanzaExcepcion")
        void nombreDuplicadoEnOtra_lanzaExcepcion() {
            Sucursal s = buildSucursal(1L, "Viejo");
            when(sucursalRepository.findById(1L)).thenReturn(Optional.of(s));

            Sucursal otra = buildSucursal(2L, "Biblioteca Central");
            when(sucursalRepository.findByNombreIgnoreCase("Biblioteca Central"))
                    .thenReturn(Optional.of(otra));

            SucursalRequest req = buildValidRequest();

            assertThrows(BusinessRuleException.class, () -> sucursalService.actualizar(1L, req));
            verify(sucursalRepository, never()).save(any(Sucursal.class));
        }

        @Test
        @DisplayName("nombreMismaSucursal_noLanzaExcepcion")
        void nombreMismaSucursal_noLanzaExcepcion() {
            Sucursal s = buildSucursal(1L, "Biblioteca Central");
            when(sucursalRepository.findById(1L)).thenReturn(Optional.of(s));
            when(sucursalRepository.findByNombreIgnoreCase("Biblioteca Central")).thenReturn(Optional.of(s));
            when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(inv -> inv.getArgument(0));

            SucursalRequest req = buildValidRequest();
            req.setNombre("Biblioteca Central");

            SucursalResponse resp = sucursalService.actualizar(1L, req);

            assertNotNull(resp);
            verify(sucursalRepository).save(any(Sucursal.class));
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> sucursalService.actualizar(99L, buildValidRequest()));
        }
    }

    @Nested
    @DisplayName("eliminarSucursal")
    class EliminarSucursalTests {

        @Test
        @DisplayName("existente_elimina")
        void existente_elimina() {
            when(sucursalRepository.existsById(1L)).thenReturn(true);

            sucursalService.eliminar(1L);

            verify(sucursalRepository).deleteById(1L);
        }

        @Test
        @DisplayName("noExistente_lanzaExcepcion")
        void noExistente_lanzaExcepcion() {
            when(sucursalRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> sucursalService.eliminar(99L));
            verify(sucursalRepository, never()).deleteById(anyLong());
        }
    }

    @Test
    @DisplayName("listarTodos_retornaLista")
    void listarTodos_retornaLista() {
        Sucursal s1 = buildSucursal(1L, "A");
        Sucursal s2 = buildSucursal(2L, "B");
        when(sucursalRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<SucursalResponse> result = sucursalService.listarTodos();

        assertEquals(2, result.size());
    }
}
