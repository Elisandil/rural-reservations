package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobjects.DateRange;

import java.util.List;

public record CreateReservationCommand(
        Long accommodationId,
        Long adminId,
        DateRange dateRange,
        Integer bedsReserved,
        String notes,
        List<Customer> customers
) {

    public CreateReservationCommand {

        if (accommodationId == null) {
            throw new InvalidDomainObjectException("CreateReservationCommand", "accommodation ID cannot be null");
        }
        if (adminId == null) {
            throw new InvalidDomainObjectException("CreateReservationCommand", "admin ID cannot be null");
        }
        if (dateRange == null) {
            throw new InvalidDomainObjectException("CreateReservationCommand", "date range cannot be null");
        }
        if (bedsReserved == null) {
            throw new InvalidDomainObjectException("CreateReservationCommand", "beds reserved cannot be null");
        }
    }
}