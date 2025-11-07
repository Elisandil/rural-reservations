package com.aogdev.rural.application.port.out.accommodation;

import com.aogdev.rural.domain.model.Accommodation;

import java.util.Optional;

public interface FindAccommodationByNamePort {
    Optional<Accommodation> findByName(String name);
}