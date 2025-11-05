package com.aogdev.rural.application.port.out.accommodationType;

import com.aogdev.rural.domain.model.AccommodationType;

import java.util.List;

public interface ListAccommodationTypesPort {
    List<AccommodationType> findAll();
}