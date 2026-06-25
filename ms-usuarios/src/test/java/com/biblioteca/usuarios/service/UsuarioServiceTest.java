package com.biblioteca.usuarios.service;

import com.biblioteca.usuarios.dto.UsuarioRequest;
import com.biblioteca.usuarios.dto.UsuarioResponse;
import com.biblioteca.usuarios.exception.BusinessRuleException;
import com.biblioteca.usuarios.exception.ResourceNotFoundException;
import com.biblioteca.usuarios.model.entity.Rol;
import com.biblioteca.usuarios.model.entity.Usuario;
import com.biblioteca.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("crearUsuario")
    class CrearUsuarioTests {

        @Test
        @DisplayName("emailExistente_lanzaExcepcion")
        void emailExistente_lanzaExcepcion() {
            // Given
            UsuarioRequest request = new UsuarioRequest();
            request.setNombre("Juan Pérez");
            request.setEmail("juan@email.com");
            request.setRol(Rol.ALUMNO);

            when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

            // When & Then
            assertThrows(BusinessRuleException.class, () -> usuarioService.crear(request));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("datosValidos_guardaUsuario")
        void datosValidos_guardaUsuario() {
            // Given
            UsuarioRequest request = new UsuarioRequest();
            request.setNombre("Juan Pérez");
            request.setEmail("juan@email.com");
            request.setTelefono("12345678");
            request.setDireccion("Calle 1");
            request.setRol(Rol.ALUMNO);

            when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });

            // When
            UsuarioResponse response = usuarioService.crear(request);

            // Then
            assertNotNull(response.getId());
            assertEquals(request.getNombre(), response.getNombre());
            assertEquals(request.getEmail(), response.getEmail());
            verify(usuarioRepository).save(any(Usuario.class));
        }
    }

    @Nested
    @DisplayName("obtenerLimite")
    class ObtenerLimiteTests {

        @Test
        @DisplayName("porRolAlumno_retorna3")
        void porRolAlumno_retorna3() {
            // Given
            Usuario usuario = new Usuario();
            usuario.setId(1L);
            usuario.setRol(Rol.ALUMNO);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            // When
            int limite = usuarioService.obtenerLimitePrestamos(1L);

            // Then
            assertEquals(3, limite);
        }

        @Test
        @DisplayName("porRolDocente_retorna5")
        void porRolDocente_retorna5() {
            // Given
            Usuario usuario = new Usuario();
            usuario.setId(1L);
            usuario.setRol(Rol.DOCENTE);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            // When
            int limite = usuarioService.obtenerLimitePrestamos(1L);

            // Then
            assertEquals(5, limite);
        }

        @Test
        @DisplayName("porRolBibliotecario_retorna10")
        void porRolBibliotecario_retorna10() {
            // Given
            Usuario usuario = new Usuario();
            usuario.setId(1L);
            usuario.setRol(Rol.BIBLIOTECARIO);

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            // When
            int limite = usuarioService.obtenerLimitePrestamos(1L);

            // Then
            assertEquals(10, limite);
        }

        @Test
        @DisplayName("usuarioNoExistente_lanzaExcepcion")
        void usuarioNoExistente_lanzaExcepcion() {
            // Given
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> usuarioService.obtenerLimitePrestamos(99L));
        }
    }

    @Nested
    @DisplayName("eliminarUsuario")
    class EliminarUsuarioTests {

        @Test
        @DisplayName("usuarioExistente_eliminaCorrectamente")
        void usuarioExistente_eliminaCorrectamente() {
            // Given
            when(usuarioRepository.existsById(1L)).thenReturn(true);

            // When
            usuarioService.eliminar(1L);

            // Then
            verify(usuarioRepository).deleteById(1L);
        }

        @Test
        @DisplayName("usuarioNoExistente_lanzaExcepcion")
        void usuarioNoExistente_lanzaExcepcion() {
            // Given
            when(usuarioRepository.existsById(99L)).thenReturn(false);

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> usuarioService.eliminar(99L));
            verify(usuarioRepository, never()).deleteById(anyLong());
        }
    }
}