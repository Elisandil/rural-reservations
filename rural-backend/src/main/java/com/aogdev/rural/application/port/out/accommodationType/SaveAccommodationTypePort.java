package com.aogdev.rural.application.port.out.accommodationType;

import com.aogdev.rural.domain.model.AccommodationType;

public interface SaveAccommodationTypePort {
    AccommodationType save(AccommodationType accommodationType);
}