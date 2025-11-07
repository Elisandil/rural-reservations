package com.aogdev.rural.application.port.out.accommodation;

import com.aogdev.rural.domain.model.Accommodation;

public interface SaveAccommodationPort {
    Accommodation save(Accommodation accommodation);
}