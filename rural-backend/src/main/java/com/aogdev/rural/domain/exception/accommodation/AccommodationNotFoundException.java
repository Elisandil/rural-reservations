package com.aogdev.rural.domain.exception.accommodation;

import com.aogdev.rural.domain.exception.DomainException;

public class AccommodationNotFoundException extends DomainException {

    public AccommodationNotFoundException(Long id) {
        super("Accommodation not found with id: " + id);
    }
}