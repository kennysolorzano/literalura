package com.literalura.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiBookDTO {
    private Integer id;
    private String title;
    private List<ApiAuthorDTO> authors;
    private List<String> languages;
    private Integer download_count;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<ApiAuthorDTO> getAuthors() { return authors; }
    public void setAuthors(List<ApiAuthorDTO> authors) { this.authors = authors; }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }

    public Integer getDownload_count() { return download_count; }
    public void setDownload_count(Integer download_count) { this.download_count = download_count; }
}
