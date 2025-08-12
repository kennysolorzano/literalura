package com.literalura.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseDTO {
    private int count;
    private String next;
    private String previous;
    private List<ApiBookDTO> results;

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public String getNext() { return next; }
    public void setNext(String next) { this.next = next; }

    public String getPrevious() { return previous; }
    public void setPrevious(String previous) { this.previous = previous; }

    public List<ApiBookDTO> getResults() { return results; }
    public void setResults(List<ApiBookDTO> results) { this.results = results; }
}
