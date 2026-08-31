package com.agi.qa.model;

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
