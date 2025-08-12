package com.literalura.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.literalura.service.dto.ApiResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class ApiClient {

    private static final String BASE = "https://gutendex.com/books";
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final HttpHeaders defaultHeaders = new HttpHeaders();

    public ApiClient() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(20).toMillis());

        this.restTemplate = new RestTemplate(factory);
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        defaultHeaders.add(HttpHeaders.USER_AGENT, "Literalura/1.0 (+https://example.invalid)");
    }

    /** Busca libros por texto libre (el método se encarga de codificar). */
    public ApiResponseDTO searchBooks(String query) {
        try {
            String encoded = URLEncoder.encode(query == null ? "" : query, StandardCharsets.UTF_8);
            String url = BASE + "?search=" + encoded + "&page=1&page_size=20";
            var req = RequestEntity.get(URI.create(url)).headers(defaultHeaders).build();
            var resp = restTemplate.exchange(req, String.class);
            return mapper.readValue(resp.getBody(), ApiResponseDTO.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error consultando Gutendex (searchBooks): " + ex.getMessage(), ex);
        }
    }

    /** Devuelve libros por idioma; luego el servicio los ordena por descargas y recorta a 'limit'. */
    public ApiResponseDTO topByLanguage(String languageCode, int limit) {
        try {
            int pageSize = Math.max(10, Math.min(limit * 2, 40)); // margen extra para elegir
            String lang = URLEncoder.encode(languageCode == null ? "" : languageCode, StandardCharsets.UTF_8);
            String url = BASE + "?languages=" + lang + "&page=1&page_size=" + pageSize;
            var req = RequestEntity.get(URI.create(url)).headers(defaultHeaders).build();
            var resp = restTemplate.exchange(req, String.class);
            return mapper.readValue(resp.getBody(), ApiResponseDTO.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error consultando Gutendex (topByLanguage): " + ex.getMessage(), ex);
        }
    }

    /** Seguir paginación por URL completa (next). */
    public ApiResponseDTO searchBooksByUrl(String url) {
        try {
            var req = RequestEntity.get(URI.create(url)).headers(defaultHeaders).build();
            var resp = restTemplate.exchange(req, String.class);
            return mapper.readValue(resp.getBody(), ApiResponseDTO.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error consultando Gutendex (searchBooksByUrl): " + ex.getMessage(), ex);
        }
    }
}
