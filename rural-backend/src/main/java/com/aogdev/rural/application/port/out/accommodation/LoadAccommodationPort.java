package com.aogdev.rural.application.port.out.accommodation;

import com.aogdev.rural.domain.model.Accommodation;

import java.util.Optional;

public interface LoadAccommodationPort {
    Optional<Accommodation> loadById(Long id);
}