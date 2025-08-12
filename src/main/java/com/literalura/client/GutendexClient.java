package com.literalura.client;

import java.util.List;

public interface GutendexClient {

    /**
     * Busca libros en Gutendex por término libre.
     * Debe devolver una lista (puedes limitar a top N internamente).
     */
    List<GutendexBook> buscar(String query);

    /**
     * DTO muy simple para mapear los campos que usamos.
     * Adáptalo si tu mapeo actual tiene otros nombres.
     */
    class GutendexBook {
        private String titulo;
        private String autorNombre;
        private String idioma;       // ej. "en"
        private int descargas;

        public GutendexBook() {}

        public GutendexBook(String titulo, String autorNombre, String idioma, int descargas) {
            this.titulo = titulo;
            this.autorNombre = autorNombre;
            this.idioma = idioma;
            this.descargas = descargas;
        }

        public String getTitulo() { return titulo; }
        public String getAutorNombre() { return autorNombre; }
        public String getIdioma() { return idioma; }
        public int getDescargas() { return descargas; }

        public void setTitulo(String titulo) { this.titulo = titulo; }
        public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }
        public void setIdioma(String idioma) { this.idioma = idioma; }
        public void setDescargas(int descargas) { this.descargas = descargas; }

        @Override public String toString() {
            return String.format("%s — %s [%s] (%d descargas)",
                    titulo, autorNombre != null ? autorNombre : "Autor desconocido",
                    idioma != null ? idioma : "-", descargas);
        }
    }
}
