package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.Reservation;

public interface SaveReservationPort {
    Reservation save(Reservation reservation);
    void delete(Long reservationId);
}
