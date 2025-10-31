package com.aogdev.rural.application.port.in;

import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobjects.DateRange;

import java.util.List;
import java.util.Optional;

public interface FindReservationUseCase {
    Optional<Reservation> findById(Long id);
    List<Reservation> findByAccommodationId(Long accommodationId);
    List<Reservation> findByDateRange(Long accommodationId, DateRange dateRange);
    List<Reservation> findByAdminId(Long adminId);
    List<Reservation> findUnpaid();
}
