package com.aogdev.rural.domain.model;

public record AccommodationType(
        Short id,
        String name,
        String description
) {

    public AccommodationType {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Accommodation type name cannot be blank");
        }
    }
}
