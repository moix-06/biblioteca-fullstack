package com.biblioteca.ejemplares.repository;

import com.biblioteca.ejemplares.model.entity.Ejemplar;
import com.biblioteca.ejemplares.model.entity.EstadoEjemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {

    Optional<Ejemplar> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    long countByLibroIdAndEstado(Long libroId, EstadoEjemplar estado);

    @Query("SELECT e FROM Ejemplar e WHERE " +
            "(:libroId IS NULL OR e.libroId = :libroId) AND " +
            "(:sucursalId IS NULL OR e.sucursalId = :sucursalId) AND " +
            "(:estado IS NULL OR e.estado = :estado)")
    List<Ejemplar> buscar(@Param("libroId") Long libroId,
                          @Param("sucursalId") Long sucursalId,
                          @Param("estado") EstadoEjemplar estado);
}
