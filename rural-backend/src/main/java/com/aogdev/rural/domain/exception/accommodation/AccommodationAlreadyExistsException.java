package com.aogdev.rural.domain.exception.accommodation;

import com.aogdev.rural.domain.exception.DomainException;

public class AccommodationAlreadyExistsException extends DomainException {

    public AccommodationAlreadyExistsException(String name) {
        super("Accommodation already exists with name: " + name);
    }
}