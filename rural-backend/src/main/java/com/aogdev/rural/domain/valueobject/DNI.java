package com.aogdev.rural.domain.valueobjects;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;

public record DNI(String value) {

    private static final String DNI_PATTERN = "^[0-9]{8}[A-Z]$|^[XYZ][0-9]{7}[A-Z]$";

    public DNI {
        if (value == null || value.isBlank()) {
            throw new InvalidDomainObjectException("DNI", "cannot be null or blank");
        }
        String normalized = value.trim().toUpperCase();

        if (!normalized.matches(DNI_PATTERN)) {
            throw new InvalidDomainObjectException("DNI", "invalid format: " + value);
        }
        value = normalized;
    }
}