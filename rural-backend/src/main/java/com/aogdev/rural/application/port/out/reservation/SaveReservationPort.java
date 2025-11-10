package com.aogdev.rural.application.port.out.reservation;

import com.aogdev.rural.domain.model.Reservation;

public interface SaveReservationPort {
    Reservation save(Reservation reservation);
}