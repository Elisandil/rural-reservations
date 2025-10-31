package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobjects.DateRange;

import java.util.List;
import java.util.Optional;

public interface LoadReservationPort {
    Optional<Reservation> loadById(Long id);
    List<Reservation> loadByAccommodationId(Long accommodationId);
    List<Reservation> loadByAdminId(Long adminId);
    List<Reservation> loadByDateRange(Long accommodationId, DateRange dateRange);
    List<Reservation> loadUnpaid();
    Boolean existsById(Long id);
}
