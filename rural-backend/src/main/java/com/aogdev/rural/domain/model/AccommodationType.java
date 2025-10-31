package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;

public record AccommodationType(
        Short id,
        String name,
        String description
) {

    public AccommodationType {
        if (name == null || name.isBlank()) {
            throw new InvalidDomainObjectException("AccommodationType", "name cannot be blank");
        }
    }
}