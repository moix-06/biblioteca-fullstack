package com.biblioteca.autores.repository;

import com.biblioteca.autores.model.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    boolean existsByNombreIgnoreCaseAndApellidoIgnoreCase(String nombre, String apellido);

    Optional<Autor> findByNombreIgnoreCaseAndApellidoIgnoreCase(String nombre, String apellido);
}