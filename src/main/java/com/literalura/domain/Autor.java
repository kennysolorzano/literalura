package com.literalura.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores", uniqueConstraints = {
        @UniqueConstraint(name = "uk_autor_nombre", columnNames = "nombre")
})
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "anio_nacimiento")
    private Integer anioNacimiento;

    @Column(name = "anio_fallecimiento")
    private Integer anioFallecimiento;

    // Mantener LAZY aquí está bien; evitamos tocar esta colección en toString()
    @OneToMany(mappedBy = "autor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore // por si algún día serializas a JSON, evita ciclos
    private List<Libro> libros = new ArrayList<>();

    // ===== getters/setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getAnioNacimiento() { return anioNacimiento; }
    public void setAnioNacimiento(Integer anioNacimiento) { this.anioNacimiento = anioNacimiento; }

    public Integer getAnioFallecimiento() { return anioFallecimiento; }
    public void setAnioFallecimiento(Integer anioFallecimiento) { this.anioFallecimiento = anioFallecimiento; }

    public List<Libro> getLibros() { return libros; }
    public void setLibros(List<Libro> libros) { this.libros = libros; }

    @Override
    public String toString() {
        String n = nombre != null ? nombre : "(sin nombre)";
        String born = anioNacimiento != null ? String.valueOf(anioNacimiento) : "¿?";
        String died = anioFallecimiento != null ? String.valueOf(anioFallecimiento) : "¿?";
        // Ej: Jane Austen (1775–1817)
        return String.format("%s (%s–%s)", n, born, died);
    }

}
