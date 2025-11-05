package com.aogdev.rural.application.port.in.accommodationType;

import com.aogdev.rural.domain.model.AccommodationType;

import java.util.List;

public interface ListAccommodationTypesUseCase {
    List<AccommodationType> listAll();
}