package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobjects.DateRange;

import java.util.List;

public record UpdateReservationCommand(
        Long reservationId,
        DateRange dateRange,
        Integer bedsReserved,
        String notes,
        List<Customer> customers
) {

    public UpdateReservationCommand {

        if (reservationId == null) {
            throw new InvalidDomainObjectException("UpdateReservationCommand", "reservation ID cannot be null");
        }
    }
}