package com.aogdev.rural.domain.exception.accommodationType;

import com.aogdev.rural.domain.exception.DomainException;

public class AccommodationTypeAlreadyExistsException extends DomainException {

    public AccommodationTypeAlreadyExistsException(String name) {
        super("Accommodation type already exists with name: " + name);
    }
}