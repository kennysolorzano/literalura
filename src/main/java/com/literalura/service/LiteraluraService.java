package com.literalura.service;

import com.literalura.domain.Autor;
import com.literalura.domain.Libro;
import com.literalura.repository.AutorRepository;
import com.literalura.repository.LibroRepository;
import com.literalura.service.dto.ApiAuthorDTO;
import com.literalura.service.dto.ApiBookDTO;
import com.literalura.service.dto.ApiResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LiteraluraService {

    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;
    private final ApiClient apiClient;

    public LiteraluraService(AutorRepository autorRepository,
                             LibroRepository libroRepository,
                             ApiClient apiClient) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
        this.apiClient = apiClient;
    }

    // ================= Búsquedas / Guardado =================

    @Transactional
    public Libro buscarYGuardarLibroPorTitulo(String titulo) {
        String q = titulo == null ? "" : titulo.trim();
        if (q.isEmpty()) return null;

        Optional<Libro> enDb = libroRepository
                .findTopByTituloContainingIgnoreCaseOrderByDescargasDesc(q);
        if (enDb.isPresent()) return enDb.get();

        ApiBookDTO mejor = elegirMejorCandidato(buscarEnApi(q), q);
        if (mejor == null) return null;

        return guardarOActualizarDesdeApi(mejor);
    }

    @Transactional
    public Optional<Libro> buscarYGuardarPorTitulo(String titulo) {
        return Optional.ofNullable(buscarYGuardarLibroPorTitulo(titulo));
    }

    @Transactional
    public List<Libro> buscarYGuardarVariosPorTitulo(String titulo, int max) {
        String q = titulo == null ? "" : titulo.trim();
        if (q.isEmpty()) return List.of();

        List<Libro> existentes = libroRepository.findAll().stream()
                .filter(l -> contiene(l.getTitulo(), q))
                .sorted(Comparator.comparingInt((Libro l) -> l.getDescargas() == null ? 0 : l.getDescargas())
                        .reversed())
                .limit(Math.max(1, max))
                .collect(Collectors.toList());

        if (existentes.size() >= max) return existentes;

        List<ApiBookDTO> api = buscarEnApi(q);
        if (api.isEmpty()) return existentes;

        List<ApiBookDTO> filtrados = api.stream()
                .filter(b -> contiene(b.getTitle(), q))
                .sorted(Comparator.comparingInt((ApiBookDTO b) -> b.getDownload_count() == null ? 0 : b.getDownload_count())
                        .reversed())
                .limit(Math.max(1, max))
                .toList();

        List<Libro> guardados = new ArrayList<>(existentes);
        for (ApiBookDTO dto : filtrados) {
            guardados.add(guardarOActualizarDesdeApi(dto));
        }
        return dedupPorTitulo(guardados);
    }

    // Importar por idioma (paginando Gutendex) — ya lo tenías, lo mantenemos
    @Transactional
    public List<Libro> importarPorIdioma(String idioma, int max) {
        if (idioma == null || idioma.isBlank() || max <= 0) return List.of();

        String url = "https://gutendex.com/books/?languages="
                + URLEncoder.encode(idioma.trim(), StandardCharsets.UTF_8);

        List<Libro> guardados = new ArrayList<>();
        int count = 0;

        while (url != null && count < max) {
            ApiResponseDTO resp = apiClient.searchBooksByUrl(url);
            if (resp == null || resp.getResults() == null || resp.getResults().isEmpty()) break;

            for (ApiBookDTO dto : resp.getResults()) {
                guardados.add(guardarOActualizarDesdeApi(dto));
                count++;
                if (count >= max) break;
            }
            url = resp.getNext();
        }
        return dedupPorTitulo(guardados);
    }

    // ================= Consultas / Estadísticas =================

    @Transactional(readOnly = true)
    public List<Libro> listarTodosLosLibros() {
        return libroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Autor> listarAutores() {
        return autorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Autor> listarAutoresVivosEn(int anio) {
        return autorRepository.autoresVivosEn(anio);
    }

    @Transactional(readOnly = true)
    public List<Libro> listarLibrosPorIdioma(String idioma) {
        return libroRepository.findByIdiomaIgnoreCase(idioma);
    }

    @Transactional(readOnly = true)
    public long contarLibrosPorIdioma(String idioma) {
        return libroRepository.countByIdiomaIgnoreCase(idioma);
    }

    // Top 10 global por descargas
    @Transactional(readOnly = true)
    public List<Libro> top10MasDescargados() {
        return libroRepository.findTop10ByOrderByDescargasDesc();
    }

    // Top N por idioma (ordenados por descargas)
    @Transactional(readOnly = true)
    public List<Libro> topPorIdioma(String idioma, int n) {
        int size = Math.max(1, n);
        Pageable page = PageRequest.of(0, size);
        return libroRepository.findByIdiomaIgnoreCaseOrderByDescargasDesc(idioma, page);
    }

    // Estadísticas de descargas (por idioma o global si idioma es null/blank)
    @Transactional(readOnly = true)
    public IntSummaryStatistics estadisticasDescargas(String idioma) {
        List<Libro> base = (idioma == null || idioma.isBlank())
                ? libroRepository.findAll()
                : libroRepository.findByIdiomaIgnoreCase(idioma);

        return base.stream()
                .map(l -> l.getDescargas() == null ? 0 : l.getDescargas())
                .mapToInt(Integer::intValue)
                .summaryStatistics();
    }


    // Buscar autor por nombre (contains, ignore case)
    @Transactional(readOnly = true)
    public List<Autor> buscarAutorPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return List.of();
        return autorRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }

    @Transactional(readOnly = true)
    public List<Autor> autoresPorNacimientoEntre(Integer desde, Integer hasta) {
        if (desde == null || hasta == null) return List.of();
        return autorRepository.findByAnioNacimientoBetween(desde, hasta);
    }

    @Transactional(readOnly = true)
    public List<Autor> autoresPorFallecimientoEntre(Integer desde, Integer hasta) {
        if (desde == null || hasta == null) return List.of();
        return autorRepository.findByAnioFallecimientoBetween(desde, hasta);
    }

    // ================= Helpers =================

    private List<ApiBookDTO> buscarEnApi(String q) {
        String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
        ApiResponseDTO resp = apiClient.searchBooks(encoded);
        if (resp == null || resp.getResults() == null) return List.of();
        return resp.getResults();
    }

    private ApiBookDTO elegirMejorCandidato(List<ApiBookDTO> resultados, String q) {
        if (resultados == null || resultados.isEmpty()) return null;
        return resultados.stream()
                .max(Comparator.<ApiBookDTO>comparingInt(b -> contiene(b.getTitle(), q) ? 1 : 0)
                        .thenComparingInt(b -> b.getDownload_count() == null ? 0 : b.getDownload_count()))
                .orElse(null);
    }

    private Libro guardarOActualizarDesdeApi(ApiBookDTO dto) {
        ApiAuthorDTO apiAutor = (dto.getAuthors() != null && !dto.getAuthors().isEmpty())
                ? dto.getAuthors().get(0) : null;
        Autor autor = mapearYObtenerAutor(apiAutor);

        String titulo = nullSafe(dto.getTitle());
        String idioma = primeroONull(dto.getLanguages());
        int descargas = dto.getDownload_count() == null ? 0 : dto.getDownload_count();

        Optional<Libro> existente = libroRepository.findTopByTituloIgnoreCase(titulo);
        if (existente.isPresent()) {
            Libro l = existente.get();
            l.setIdioma(idioma);
            l.setDescargas(descargas);
            l.setAutor(autor);
            return libroRepository.save(l);
        }

        Libro nuevo = new Libro();
        nuevo.setTitulo(titulo);
        nuevo.setIdioma(idioma);
        nuevo.setDescargas(descargas);
        nuevo.setAutor(autor);
        return libroRepository.save(nuevo);
    }

    private Autor mapearYObtenerAutor(ApiAuthorDTO apiAutor) {
        String nombre = apiAutor != null ? nullSafe(apiAutor.getName()) : "Autor desconocido";
        Optional<Autor> existente = autorRepository.findByNombreIgnoreCase(nombre);
        if (existente.isPresent()) {
            Autor a = existente.get();
            if (a.getAnioNacimiento() == null && apiAutor != null) a.setAnioNacimiento(apiAutor.getBirth_year());
            if (a.getAnioFallecimiento() == null && apiAutor != null) a.setAnioFallecimiento(apiAutor.getDeath_year());
            return autorRepository.save(a);
        }
        Autor nuevo = new Autor();
        nuevo.setNombre(nombre);
        if (apiAutor != null) {
            nuevo.setAnioNacimiento(apiAutor.getBirth_year());
            nuevo.setAnioFallecimiento(apiAutor.getDeath_year());
        }
        return autorRepository.save(nuevo);
    }

    private static boolean contiene(String titulo, String q) {
        if (titulo == null || q == null) return false;
        return titulo.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT));
    }

    private static String primeroONull(List<String> lista) {
        return (lista == null || lista.isEmpty()) ? null : lista.get(0);
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static List<Libro> dedupPorTitulo(List<Libro> libros) {
        Map<String, Libro> m = new LinkedHashMap<>();
        for (Libro l : libros) {
            String k = l.getTitulo() == null ? "" : l.getTitulo().toLowerCase(Locale.ROOT);
            m.putIfAbsent(k, l);
        }
        return new ArrayList<>(m.values());
    }
}
