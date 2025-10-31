package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.Accommodation;

public interface SaveAccommodationPort {
    Accommodation save(Accommodation accommodation);
    void delete(Long accommodationId);
}
