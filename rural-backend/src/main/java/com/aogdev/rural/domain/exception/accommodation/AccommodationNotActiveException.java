package com.aogdev.rural.domain.exception.accommodation;

import com.aogdev.rural.domain.exception.DomainException;

public class AccommodationNotActiveException extends DomainException {

    public AccommodationNotActiveException(Long id) {
        super("Accommodation is not active with id: " + id);
    }
}