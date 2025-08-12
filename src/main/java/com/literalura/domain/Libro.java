package com.literalura.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 10)
    private String idioma;

    private Integer descargas;

    // EAGER para evitar LazyInitializationException al imprimir el autor en toString()
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    // ===== getters/setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public Integer getDescargas() { return descargas; }
    public void setDescargas(Integer descargas) { this.descargas = descargas; }

    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    @Override
    public String toString() {
        String autorNombre = (autor != null && autor.getNombre() != null) ? autor.getNombre() : "Autor desconocido";
        String lang = (idioma != null ? idioma : "—");
        String down = (descargas != null ? String.valueOf(descargas) : "0");
        // Ej: Frankenstein — Mary Shelley [en] ↑95,343
        return String.format("%s — %s [%s] ↑%,d", 
                titulo != null ? titulo : "(sin título)", autorNombre, lang, Integer.parseInt(down));
    }

}
