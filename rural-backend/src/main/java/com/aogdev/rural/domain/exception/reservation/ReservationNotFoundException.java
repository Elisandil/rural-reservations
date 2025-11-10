package com.aogdev.rural.domain.exception.reservation;

import com.aogdev.rural.domain.exception.DomainException;

public class ReservationNotFoundException extends DomainException {

    public ReservationNotFoundException(Long id) {
        super("Reservation not found with id: " + id);
    }
}