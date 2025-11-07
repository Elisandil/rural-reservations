package com.aogdev.rural.application.port.in.accommodation;

import com.aogdev.rural.domain.model.Accommodation;

import java.util.Optional;

public interface FindAccommodationUseCase {
    Optional<Accommodation> findByName(String name);
}