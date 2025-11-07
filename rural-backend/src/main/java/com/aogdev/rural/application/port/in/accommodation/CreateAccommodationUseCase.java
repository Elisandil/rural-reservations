package com.aogdev.rural.application.port.in.accommodation;

import com.aogdev.rural.domain.model.Accommodation;

public interface CreateAccommodationUseCase {
    Accommodation create(CreateAccommodationCommand command);
}