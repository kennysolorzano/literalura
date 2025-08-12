package com.literalura.console;

import com.literalura.domain.Autor;
import com.literalura.domain.Libro;
import com.literalura.service.LiteraluraService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {

    private final LiteraluraService service;
    private final Scanner teclado = new Scanner(System.in);

    public Principal(LiteraluraService service) {
        this.service = service;
    }

    public void mostrarMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("================= LiterAlura =================");
            System.out.println("1) Buscar libro por título (API → Guardar en DB)");
            System.out.println("2) Listar todos los libros");
            System.out.println("3) Listar autores");
            System.out.println("4) Listar autores vivos en un año");
            System.out.println("5) Listar libros por idioma");
            System.out.println("6) Contar libros por idioma (estadística)");
            System.out.println("0) Salir");
            System.out.println("==============================================");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(teclado.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> buscarLibro();
                case 2 -> listarLibros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresVivos();
                case 5 -> listarPorIdioma();
                case 6 -> contarPorIdioma();
                case 0 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void buscarLibro() {
        System.out.print("Ingrese título del libro a buscar: ");
        String titulo = teclado.nextLine().trim();

        // 1) Guardar el mejor match
        Optional<Libro> res = service.buscarYGuardarPorTitulo(titulo);
        if (res.isPresent()) {
            Libro l = res.get();
            System.out.println("\n✓ Mejor coincidencia guardada/actualizada:");
            System.out.printf("Libro{id=%d, titulo='%s', idioma='%s', descargas=%d, autor='%s'}%n",
                    l.getId(), l.getTitulo(), l.getIdioma(), l.getDescargas(),
                    l.getAutor() != null ? l.getAutor().getNombre() : "(desconocido)");
        } else {
            System.out.println("\n✗ No se encontró libro para ese título.");
        }

        // 2) Ofrecer guardar varios
        System.out.print("\n¿Deseas intentar guardar varias coincidencias? (S/N): ");
        String answer = teclado.nextLine().trim();
        if (answer.equalsIgnoreCase("S")) {
            System.out.print("¿Cuántos resultados máximo quieres guardar? (ej: 5): ");
            int max = 5;
            try {
                max = Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException ignored) {}

            List<Libro> varios = service.buscarYGuardarVariosPorTitulo(titulo, Math.max(1, max));
            if (varios.isEmpty()) {
                System.out.println("No se encontraron coincidencias adicionales.");
            } else {
                System.out.printf("Se guardaron/actualizaron %d resultados:%n", varios.size());
                varios.forEach(l -> System.out.printf("- %s [%s] - %s (descargas: %d)%n",
                        l.getTitulo(),
                        l.getIdioma(),
                        l.getAutor() != null ? l.getAutor().getNombre() : "(desconocido)",
                        l.getDescargas() == null ? 0 : l.getDescargas()));
            }
        }
        System.out.println();
    }

    private void listarLibros() {
        List<Libro> libros = service.listarTodosLosLibros();
        if (libros.isEmpty()) {
            System.out.println("\n(No hay libros en la base de datos)\n");
            return;
        }
        System.out.println("\n📚 Lista de todos los libros:");
        libros.forEach(l -> System.out.printf("- %s [%s] - %s (descargas: %d)%n",
                l.getTitulo(),
                l.getIdioma(),
                l.getAutor() != null ? l.getAutor().getNombre() : "(desconocido)",
                l.getDescargas() == null ? 0 : l.getDescargas()));
        System.out.println();
    }

    private void listarAutores() {
        List<Autor> autores = service.listarAutores();
        if (autores.isEmpty()) {
            System.out.println("\n(No hay autores en la base de datos)\n");
            return;
        }
        System.out.println("\n👤 Lista de autores:");
        autores.forEach(a -> System.out.printf("- %s (%s–%s)%n",
                a.getNombre(),
                a.getAnioNacimiento(),
                a.getAnioFallecimiento()));
        System.out.println();
    }

    private void listarAutoresVivos() {
        System.out.print("Ingrese año: ");
        int anio = Integer.parseInt(teclado.nextLine());
        List<Autor> autores = service.listarAutoresVivosEn(anio);
        if (autores.isEmpty()) {
            System.out.println("\nNo hay autores vivos en ese año (según registros).\n");
            return;
        }
        autores.forEach(a ->
                System.out.printf("- %s (%s–%s)%n", a.getNombre(),
                        a.getAnioNacimiento(), a.getAnioFallecimiento()));
        System.out.println();
    }

    private void listarPorIdioma() {
        System.out.print("Ingrese idioma (código ISO, ej: en, es, fr): ");
        String idioma = teclado.nextLine();
        List<Libro> libros = service.listarLibrosPorIdioma(idioma);
        System.out.printf("%nLibros en '%s' (total: %d)%n", idioma, libros.size());
        libros.forEach(l ->
                System.out.printf("- %s [%s] - %s%n",
                        l.getTitulo(), l.getIdioma(),
                        l.getAutor() != null ? l.getAutor().getNombre() : "(desconocido)"));
        System.out.println();
    }

    private void contarPorIdioma() {
        System.out.print("Ingrese idioma (código ISO): ");
        String idioma = teclado.nextLine();
        long total = service.contarLibrosPorIdioma(idioma);
        System.out.printf("Total libros en '%s': %d%n%n", idioma, total);
    }
}
// Nota: Este código es parte de un sistema de gestión de libros y autores,