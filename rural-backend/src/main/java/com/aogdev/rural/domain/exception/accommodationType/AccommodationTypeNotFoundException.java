package com.aogdev.rural.domain.exception.accommodationType;

import com.aogdev.rural.domain.exception.DomainException;

public class AccommodationTypeNotFoundException extends DomainException {

    public AccommodationTypeNotFoundException(Short id) {
        super("Accommodation type not found with id: " + id);
    }
}