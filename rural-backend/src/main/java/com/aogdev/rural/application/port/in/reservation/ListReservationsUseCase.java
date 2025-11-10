package com.aogdev.rural.application.port.in.reservation;

import com.aogdev.rural.domain.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ListReservationsUseCase {
    List<Reservation> listAll();
    List<Reservation> listByAccommodation(Long accommodationId);
    List<Reservation> listByAdmin(Long adminId);
    List<Reservation> listByDateRange(LocalDate startDate, LocalDate endDate);
    List<Reservation> listUnpaid();
}