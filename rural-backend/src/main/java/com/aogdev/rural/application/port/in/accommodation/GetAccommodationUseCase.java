package com.aogdev.rural.application.port.in.accommodation;

import com.aogdev.rural.domain.model.Accommodation;

public interface GetAccommodationUseCase {
    Accommodation getById(Long id);
}