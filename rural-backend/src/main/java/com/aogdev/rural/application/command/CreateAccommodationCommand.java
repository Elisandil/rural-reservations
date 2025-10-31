package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.Money;

public record CreateAccommodationCommand(
        Short accommodationTypeId,
        String name,
        Money pricePerNight,
        Integer bedCapacity
) {

    public CreateAccommodationCommand {

        if (accommodationTypeId == null) {
            throw new InvalidDomainObjectException("CreateAccommodationCommand", "accommodation type ID cannot be null");
        }
        if (name == null) {
            throw new InvalidDomainObjectException("CreateAccommodationCommand", "name cannot be null");
        }
        if (pricePerNight == null) {
            throw new InvalidDomainObjectException("CreateAccommodationCommand", "price per night cannot be null");
        }
        if (bedCapacity == null || bedCapacity <= 0) {
            throw new InvalidDomainObjectException("CreateAccommodationCommand", "bed capacity must be positive");
        }
    }
}