package com.aogdev.rural.application.port.out.accommodation;

import com.aogdev.rural.domain.model.Accommodation;

import java.util.List;

public interface ListAccommodationsPort {
    List<Accommodation> findAll();
    List<Accommodation> findAllActive();
    List<Accommodation> findByAccommodationTypeId(Short accommodationTypeId);
}