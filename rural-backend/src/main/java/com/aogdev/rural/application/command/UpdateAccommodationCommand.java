package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.Money;

public record UpdateAccommodationCommand(
        Long accommodationId,
        String name,
        Money pricePerNight,
        Integer bedCapacity
) {

    public UpdateAccommodationCommand {

        if (accommodationId == null) {
            throw new InvalidDomainObjectException("UpdateAccommodationCommand", "accommodation ID cannot be null");
        }
    }
}