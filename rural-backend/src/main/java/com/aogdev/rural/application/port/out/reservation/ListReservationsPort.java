package com.aogdev.rural.application.port.out.reservation;

import com.aogdev.rural.domain.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ListReservationsPort {
    List<Reservation> findAll();
    List<Reservation> findByAccommodationId(Long accommodationId);
    List<Reservation> findByAdminId(Long adminId);
    List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Reservation> findUnpaid();
}