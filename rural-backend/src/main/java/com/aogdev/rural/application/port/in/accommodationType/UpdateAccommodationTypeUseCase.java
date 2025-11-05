package com.aogdev.rural.application.port.in.accommodationType;

import com.aogdev.rural.domain.model.AccommodationType;

public interface UpdateAccommodationTypeUseCase {
    AccommodationType update(Short id, String name, String description);
}