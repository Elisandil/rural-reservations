package com.aogdev.rural.application.port.in.accommodation;

import com.aogdev.rural.domain.model.Accommodation;

import java.util.List;

public interface ListAccommodationsUseCase {
    List<Accommodation> listAll();
    List<Accommodation> listActive();
    List<Accommodation> listByAccommodationType(Short accommodationTypeId);
}