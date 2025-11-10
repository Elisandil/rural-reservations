package com.aogdev.rural.application.port.out.reservation;

import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.DateRange;

import java.util.List;

public interface FindOverlappingReservationsPort {
    List<Reservation> findOverlapping(Long accommodationId, DateRange dateRange, Long excludeReservationId);
}