package com.aogdev.rural.application.port.in.accommodation;

import com.aogdev.rural.domain.valueobject.Money;

public record UpdateAccommodationCommand(
        Long id,
        Short accommodationTypeId,
        String name,
        Money pricePerNight,
        Integer bedCapacity
) {
    public UpdateAccommodationCommand {

        if (id == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null");
        }
        if (accommodationTypeId == null) {
            throw new IllegalArgumentException("Accommodation type ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (pricePerNight == null) {
            throw new IllegalArgumentException("Price per night cannot be null");
        }
        if (bedCapacity == null || bedCapacity <= 0) {
            throw new IllegalArgumentException("Bed capacity must be positive");
        }
    }
}