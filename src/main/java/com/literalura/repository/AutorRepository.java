package com.literalura.repository;

import com.literalura.domain.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombreIgnoreCase(String nombre);

    List<Autor> findByNombreContainingIgnoreCase(String nombre);

    // Vivos en un año dado: nacimiento <= año y (fallecimiento > año o null)
    @Query("""
           select a from Autor a
           where (a.anioNacimiento is null or a.anioNacimiento <= :anio)
             and (a.anioFallecimiento is null or a.anioFallecimiento > :anio)
           """)
    List<Autor> autoresVivosEn(int anio);

    // Rangos
    List<Autor> findByAnioNacimientoBetween(Integer desde, Integer hasta);

    List<Autor> findByAnioFallecimientoBetween(Integer desde, Integer hasta);
}
