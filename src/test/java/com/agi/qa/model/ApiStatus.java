package com.agi.qa.model;

/**
 * The {@code status} field returned by every Dog API response.
 */
public enum ApiStatus {
    SUCCESS("success"),
    ERROR("error");

    private final String value;

    ApiStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
