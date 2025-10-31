package com.aogdev.rural.application.port.in;

import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.valueobjects.DateRange;

import java.util.List;
import java.util.Optional;

public interface FindAccommodationUseCase {
    Optional<Accommodation> findById(Long id);
    List<Accommodation> findAll();
    List<Accommodation> findActive();
    List<Accommodation> findByType(Short typeId);
    List<Accommodation> findAvailable(DateRange dateRange, Integer bedsRequired);
}
