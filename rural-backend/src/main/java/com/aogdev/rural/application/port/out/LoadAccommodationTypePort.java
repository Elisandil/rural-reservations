package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.AccommodationType;

import java.util.List;
import java.util.Optional;

public interface LoadAccommodationTypePort {
    Optional<AccommodationType> loadById(Short id);
    List<AccommodationType> loadAll();
    Boolean existsById(Short id);
}
