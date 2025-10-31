package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.Money;

public record Accommodation(
        Long id,
        AccommodationType accommodationType,
        String name,
        Money pricePerNight,
        Integer bedCapacity,
        Boolean active
) {

    public Accommodation {

        if (name == null || name.isBlank()) {
            throw new InvalidDomainObjectException("Accommodation", "name cannot be empty");
        }
        if (pricePerNight == null) {
            throw new InvalidDomainObjectException("Accommodation", "price per night cannot be null");
        }
        if (bedCapacity == null || bedCapacity <= 0) {
            throw new InvalidDomainObjectException("Accommodation", "bed capacity must be positive");
        }
        if (active == null) {
            active = true;
        }
    }

    public Money calculateTotalPrice(int nights) {
        if (nights <= 0) {
            throw new InvalidDomainObjectException("Accommodation", "nights must be positive");
        }
        return pricePerNight.multiply(nights);
    }

    public boolean isActive() {
        return active != null && active;
    }

    public boolean hasCapacityFor(int beds) {
        return bedCapacity == null || bedCapacity >= beds;
    }
}