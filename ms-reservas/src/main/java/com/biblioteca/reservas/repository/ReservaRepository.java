package com.biblioteca.reservas.repository;

import com.biblioteca.reservas.model.entity.EstadoReserva;
import com.biblioteca.reservas.model.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByLibroIdOrderByFechaCreacionAsc(Long libroId);

    List<Reserva> findByUsuarioIdOrderByFechaCreacionAsc(Long usuarioId);

    List<Reserva> findByEstadoOrderByFechaCreacionAsc(EstadoReserva estado);

    boolean existsByUsuarioIdAndLibroIdAndEstado(Long usuarioId, Long libroId, EstadoReserva estado);

    @Query("SELECT r FROM Reserva r WHERE " +
            "(:libroId IS NULL OR r.libroId = :libroId) AND " +
            "(:usuarioId IS NULL OR r.usuarioId = :usuarioId) AND " +
            "(:estado IS NULL OR r.estado = :estado) " +
            "ORDER BY r.fechaCreacion ASC")
    List<Reserva> buscar(@Param("libroId") Long libroId,
                         @Param("usuarioId") Long usuarioId,
                         @Param("estado") EstadoReserva estado);
}
