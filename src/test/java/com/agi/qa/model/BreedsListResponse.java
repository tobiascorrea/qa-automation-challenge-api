package com.agi.qa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Response of {@code GET /breeds/list/all}.
 *
 * <p>The {@code message} is a map where each key is a breed and the value is
 * the list of its sub-breeds (possibly empty).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BreedsListResponse {

    private Map<String, List<String>> message;
    private String status;

    public Map<String, List<String>> getMessage() {
        return message == null ? Collections.emptyMap() : message;
    }

    public void setMessage(Map<String, List<String>> message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
