package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.Accommodation;

import java.util.List;
import java.util.Optional;

public interface LoadAccommodationPort {
    Optional<Accommodation> loadById(Long id);
    List<Accommodation> loadAll();
    List<Accommodation> loadActive();
    List<Accommodation> loadByType(Short typeId);
    Boolean existsById(Long id);
}
