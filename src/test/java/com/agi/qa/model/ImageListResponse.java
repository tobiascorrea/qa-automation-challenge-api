package com.agi.qa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

/**
 * Response of {@code GET /breed/{breed}/images}.
 *
 * <p>The {@code message} is the list of image URLs for the requested breed.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageListResponse {

    private List<String> message;
    private String status;

    public List<String> getMessage() {
        return message == null ? Collections.emptyList() : message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
