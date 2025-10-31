package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.valueobjects.DateRange;

import java.util.List;

public interface CheckAvailabilityPort {
    Boolean isAvailable(Long accommodationId, DateRange dateRange, Integer bedsRequired);
    Integer availableBeds(Long accommodationId, DateRange dateRange);
    List<Accommodation> findAvailableAccommodations(DateRange dateRange, Integer bedsRequired);
}
