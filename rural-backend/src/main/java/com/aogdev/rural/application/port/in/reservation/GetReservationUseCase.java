package com.aogdev.rural.application.port.in.reservation;

import com.aogdev.rural.domain.model.Reservation;

public interface GetReservationUseCase {
    Reservation getById(Long id);
}