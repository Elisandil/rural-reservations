package com.aogdev.rural.domain.exception.reservation;

import com.aogdev.rural.domain.exception.DomainException;

public class ReservationOverlapException extends DomainException {

    public ReservationOverlapException(Long accommodationId) {
        super("Reservation dates overlap with existing reservation for accommodation: " + accommodationId);
    }
}