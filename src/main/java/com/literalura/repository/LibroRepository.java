package com.literalura.repository;

import com.literalura.domain.Libro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findTopByTituloIgnoreCase(String titulo);

    Optional<Libro> findTopByTituloContainingIgnoreCaseOrderByDescargasDesc(String titulo);

    List<Libro> findByIdiomaIgnoreCase(String idioma);

    long countByIdiomaIgnoreCase(String idioma);

    // Top 10 global por descargas
    List<Libro> findTop10ByOrderByDescargasDesc();

    // Top N por idioma
    List<Libro> findByIdiomaIgnoreCaseOrderByDescargasDesc(String idioma, Pageable pageable);
}
