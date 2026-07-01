package com.biblioteca.resenas.repository;

import com.biblioteca.resenas.model.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    boolean existsByUsuarioIdAndLibroId(Long usuarioId, Long libroId);

    List<Resena> findByLibroIdOrderByFechaCreacionDesc(Long libroId);

    List<Resena> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    List<Resena> findByCalificacionOrderByFechaCreacionDesc(Integer calificacion);

    @Query("SELECT r FROM Resena r WHERE " +
            "(:libroId IS NULL OR r.libroId = :libroId) AND " +
            "(:usuarioId IS NULL OR r.usuarioId = :usuarioId) AND " +
            "(:calificacion IS NULL OR r.calificacion = :calificacion) " +
            "ORDER BY r.fechaCreacion DESC")
    List<Resena> buscar(@Param("libroId") Long libroId,
                        @Param("usuarioId") Long usuarioId,
                        @Param("calificacion") Integer calificacion);
}
