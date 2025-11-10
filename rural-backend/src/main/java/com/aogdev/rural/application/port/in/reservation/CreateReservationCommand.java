package com.aogdev.rural.application.port.in.reservation;

import com.aogdev.rural.domain.valueobject.DateRange;

public record CreateReservationCommand(
        Long accommodationId,
        Long adminId,
        DateRange dateRange,
        Integer bedsReserved,
        String notes
) {
    public CreateReservationCommand {

        if (accommodationId == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null");
        }
        if (adminId == null) {
            throw new IllegalArgumentException("Admin ID cannot be null");
        }
        if (dateRange == null) {
            throw new IllegalArgumentException("Date range cannot be null");
        }
        if (bedsReserved == null || bedsReserved <= 0) {
            throw new IllegalArgumentException("Beds reserved must be positive");
        }
    }
}