package com.biblioteca.editoriales.repository;

import com.biblioteca.editoriales.model.entity.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Editorial> findByNombreIgnoreCase(String nombre);
}