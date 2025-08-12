package com.literalura.cli;

import com.literalura.domain.Autor;
import com.literalura.domain.Libro;
import com.literalura.service.LiteraluraService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Component
public class AplicacionConsola implements CommandLineRunner {

    private final LiteraluraService service;

    public AplicacionConsola(LiteraluraService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        try (Scanner sc = new Scanner(System.in)) {
            boolean continuar = true;
            while (continuar) {
                mostrarMenu();
                System.out.print("Seleccione una opción: ");
                String opcion = sc.nextLine().trim();
                try {
                    switch (opcion) {
                        case "1" -> {
                            System.out.print("Ingrese título del libro a buscar: ");
                            String titulo = sc.nextLine().trim();
                            var libro = service.buscarYGuardarLibroPorTitulo(titulo);
                            if (libro != null) {
                                System.out.println("\n✅ Libro guardado/actualizado:\n" + formatLibro(libro));
                            } else {
                                System.out.println("\n⚠️ No se encontró libro para ese título.");
                            }
                        }
                        case "2" -> {
                            System.out.println("\n📚 Lista de todos los libros:");
                            service.listarTodosLosLibros().forEach(l -> System.out.println("• " + formatLibro(l)));
                        }
                        case "3" -> {
                            System.out.println("\n👤 Lista de autores:");
                            service.listarAutores().forEach(a -> System.out.println("• " + formatAutor(a)));
                        }
                        case "4" -> {
                            System.out.print("Ingrese año para autores vivos: ");
                            int anio = Integer.parseInt(sc.nextLine().trim());
                            var autores = service.listarAutoresVivosEn(anio);
                            if (autores.isEmpty()) {
                                System.out.println("\nNo hay autores vivos en ese año (según registros).\n");
                            } else {
                                autores.forEach(a -> System.out.println("• " + formatAutor(a)));
                            }
                        }
                        case "5" -> {
                            System.out.print("Idioma (ej: 'en' o 'es'): ");
                            String idioma = sc.nextLine().trim();
                            System.out.print("¿Cuántos importar? ");
                            int max = Integer.parseInt(sc.nextLine().trim());
                            List<Libro> importados = service.importarPorIdioma(idioma, max);
                            System.out.printf("%n✅ Importados %d libros de idioma '%s'%n", importados.size(), idioma);
                            importados.forEach(l -> System.out.println("• " + formatLibro(l)));
                        }
                        case "6" -> {
                            System.out.print("Ingrese idioma para contar (ej: 'en' o 'es'): ");
                            String lang = sc.nextLine().trim();
                            long cantidad = service.contarLibrosPorIdioma(lang);
                            System.out.printf("\n📊 Cantidad de libros en '%s': %d\n", lang, cantidad);
                        }
                        case "7" -> { // Top 10 global
                            System.out.println("\n🏆 Top 10 libros más descargados (global):");
                            service.top10MasDescargados()
                                   .forEach(l -> System.out.println("• " + formatLibro(l)));
                        }
                        case "8" -> { // Top N por idioma
                            System.out.print("Idioma (ej: 'en' o 'es'): ");
                            String idioma = sc.nextLine().trim();
                            System.out.print("¿Cuántos (Top N)? ");
                            int n = Integer.parseInt(sc.nextLine().trim());
                            System.out.printf("%n🏆 Top %d en '%s':%n", n, idioma);
                            service.topPorIdioma(idioma, n)
                                   .forEach(l -> System.out.println("• " + formatLibro(l)));
                        }
                        case "9" -> { // Estadísticas
                            System.out.print("Idioma (vacío = global): ");
                            String idioma = sc.nextLine().trim();
                            IntSummaryStatistics s = service.estadisticasDescargas(idioma);

                            var nf = NumberFormat.getIntegerInstance(Locale.getDefault());
                            System.out.println("\n📈 Estadísticas de descargas" + (idioma.isBlank() ? " (global)" : " ['" + idioma + "']"));
                            System.out.println("• count = " + s.getCount());
                            System.out.println("• sum   = " + nf.format((long) s.getSum()));
                            System.out.println("• min   = " + nf.format((long) s.getMin()));
                            System.out.println("• max   = " + nf.format((long) s.getMax()));
                            System.out.println("• avg   = " + nf.format(Math.round(s.getAverage())));
                        }
                        case "10" -> { // Buscar autor por nombre
                            System.out.print("Nombre (o parte): ");
                            String nombre = sc.nextLine().trim();
                            var autores = service.buscarAutorPorNombre(nombre);
                            if (autores.isEmpty()) {
                                System.out.println("\nNo se encontraron autores.\n");
                            } else {
                                System.out.println("\nAutores encontrados:");
                                autores.forEach(a -> System.out.println("• " + formatAutor(a)));
                            }
                        }
                        case "11" -> { // Listados por rangos
                            System.out.print("Rango nacimiento (desde): ");
                            Integer nd = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Rango nacimiento (hasta): ");
                            Integer nh = Integer.parseInt(sc.nextLine().trim());
                            var nac = service.autoresPorNacimientoEntre(nd, nh);
                            System.out.printf("%n👶 Autores nacidos entre %d y %d:%n", nd, nh);
                            nac.forEach(a -> System.out.println("• " + formatAutor(a)));

                            System.out.print("\nRango fallecimiento (desde): ");
                            Integer fd = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Rango fallecimiento (hasta): ");
                            Integer fh = Integer.parseInt(sc.nextLine().trim());
                            var fal = service.autoresPorFallecimientoEntre(fd, fh);
                            System.out.printf("%n🕯️  Autores fallecidos entre %d y %d:%n", fd, fh);
                            fal.forEach(a -> System.out.println("• " + formatAutor(a)));
                        }
                        case "0" -> {
                            continuar = false;
                            System.out.println("\nHasta luego 👋");
                        }
                        default -> System.out.println("\nOpción inválida. Intente nuevamente.\n");
                    }
                } catch (Exception e) {
                    System.out.println("\n❌ Error: " + e.getMessage());
                }
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\n================= LiterAlura =================");
        System.out.println("1) Buscar libro por título (API → Guardar en DB)");
        System.out.println("2) Listar todos los libros");
        System.out.println("3) Listar autores");
        System.out.println("4) Listar autores vivos en un año");
        System.out.println("5) Importar libros por idioma (API, paginado)");
        System.out.println("6) Contar libros por idioma");
        System.out.println("7) Top 10 libros más descargados (DB)");
        System.out.println("8) Top N por idioma (DB)");
        System.out.println("9) Estadísticas de descargas (global / por idioma)");
        System.out.println("10) Buscar autor por nombre (DB)");
        System.out.println("11) Listar autores por rangos (nac/fall) (DB)");
        System.out.println("0) Salir");
        System.out.println("==============================================");
    }

    private String formatLibro(Libro l) {
        String autor = (l.getAutor() != null && l.getAutor().getNombre() != null)
                ? l.getAutor().getNombre() : "(desconocido)";
        String lang = l.getIdioma() == null ? "?" : l.getIdioma();
        int dl = l.getDescargas() == null ? 0 : l.getDescargas();
        return "%s — %s [%s] ↑%d".formatted(l.getTitulo(), autor, lang, dl);
    }

    private String formatAutor(Autor a) {
        String n = a.getNombre() == null ? "(sin nombre)" : a.getNombre();
        String nac = a.getAnioNacimiento() == null ? "¿?" : a.getAnioNacimiento().toString();
        String fal = a.getAnioFallecimiento() == null ? "¿?" : a.getAnioFallecimiento().toString();
        return "%s (%s–%s)".formatted(n, nac, fal);
    }
}
