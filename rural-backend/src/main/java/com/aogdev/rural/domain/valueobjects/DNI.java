package com.aogdev.rural.domain.valueobjects;

public record DNI(String value) {

    private static final String DNI_PATTERN = "^[0-9]{8}[A-Z]$|^[XYZ][0-9]{7}[A-Z]$";

    public DNI {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        String normalized = value.trim().toUpperCase();

        if (!normalized.matches(DNI_PATTERN)) {
            throw new IllegalArgumentException("Invalid DNI format: " + value);
        }
        value = normalized;
    }
}
