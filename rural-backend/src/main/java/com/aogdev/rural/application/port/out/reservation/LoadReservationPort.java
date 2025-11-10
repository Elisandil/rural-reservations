package com.aogdev.rural.application.port.out.reservation;

import com.aogdev.rural.domain.model.Reservation;

import java.util.Optional;

public interface LoadReservationPort {
    Optional<Reservation> loadById(Long id);
}