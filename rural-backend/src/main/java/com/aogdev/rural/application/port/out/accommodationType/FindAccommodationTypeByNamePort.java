package com.aogdev.rural.application.port.out.accommodationType;

import com.aogdev.rural.domain.model.AccommodationType;

import java.util.Optional;

public interface FindAccommodationTypeByNamePort {
    Optional<AccommodationType> findByName(String name);
}