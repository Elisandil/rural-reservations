package com.aogdev.rural.application.port.in.accommodationType;

import com.aogdev.rural.domain.model.AccommodationType;

public interface CreateAccommodationTypeUseCase {
    AccommodationType create(String name, String description);
}