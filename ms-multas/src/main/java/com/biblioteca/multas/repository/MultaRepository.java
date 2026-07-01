package com.biblioteca.multas.repository;

import com.biblioteca.multas.model.entity.EstadoMulta;
import com.biblioteca.multas.model.entity.Multa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Long> {

    boolean existsByPrestamoIdAndEstado(Long prestamoId, EstadoMulta estado);

    List<Multa> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    List<Multa> findByEstadoOrderByFechaCreacionDesc(EstadoMulta estado);

    @Query("SELECT m FROM Multa m WHERE " +
            "(:usuarioId IS NULL OR m.usuarioId = :usuarioId) AND " +
            "(:prestamoId IS NULL OR m.prestamoId = :prestamoId) AND " +
            "(:estado IS NULL OR m.estado = :estado) " +
            "ORDER BY m.fechaCreacion DESC")
    List<Multa> buscar(@Param("usuarioId") Long usuarioId,
                       @Param("prestamoId") Long prestamoId,
                       @Param("estado") EstadoMulta estado);
}
