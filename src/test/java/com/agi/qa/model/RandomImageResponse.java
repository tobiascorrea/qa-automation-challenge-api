package com.agi.qa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response of {@code GET /breeds/image/random}.
 *
 * <p>The {@code message} is a single image URL.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomImageResponse {

    private String message;
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
