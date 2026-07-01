package com.biblioteca.prestamos.repository;

import com.biblioteca.prestamos.model.entity.EstadoPrestamo;
import com.biblioteca.prestamos.model.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuarioId(Long usuarioId);

    List<Prestamo> findByEstado(EstadoPrestamo estado);

    List<Prestamo> findByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    long countByUsuarioIdAndEstadoIn(Long usuarioId, List<EstadoPrestamo> estados);

    boolean existsByUsuarioIdAndEjemplarIdAndEstado(Long usuarioId, Long ejemplarId, EstadoPrestamo estado);

    @Query("SELECT p FROM Prestamo p WHERE " +
            "(:usuarioId IS NULL OR p.usuarioId = :usuarioId) AND " +
            "(:estado IS NULL OR p.estado = :estado)")
    List<Prestamo> buscar(@Param("usuarioId") Long usuarioId,
                          @Param("estado") EstadoPrestamo estado);
}
