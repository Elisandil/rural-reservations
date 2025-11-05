package com.aogdev.rural.application.port.in.accommodationType;

import com.aogdev.rural.domain.model.AccommodationType;

import java.util.Optional;

public interface FindAccommodationTypeUseCase {
    Optional<AccommodationType> findByName(String name);
}