package com.aogdev.rural.application.port.out.accommodationType;

import com.aogdev.rural.domain.model.AccommodationType;

import java.util.Optional;

public interface LoadAccommodationTypePort {
    Optional<AccommodationType> loadById(Short id);
}