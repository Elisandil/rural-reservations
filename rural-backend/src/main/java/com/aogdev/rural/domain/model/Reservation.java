package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.DateRange;
import com.aogdev.rural.domain.valueobjects.Money;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record Reservation(
        Long id,
        Long accommodationId,
        Long adminId,
        DateRange dateRange,
        Integer bedsReserved,
        Money totalPrice,
        Boolean paid,
        LocalDate bookingDate,
        String notes,
        List<Customer> customers
) {

    public Reservation {

        if (accommodationId == null) {
            throw new InvalidDomainObjectException("Reservation", "accommodation ID cannot be null");
        }
        if (adminId == null) {
            throw new InvalidDomainObjectException("Reservation", "admin ID cannot be null");
        }
        if (dateRange == null) {
            throw new InvalidDomainObjectException("Reservation", "date range cannot be null");
        }
        if (bedsReserved == null || bedsReserved <= 0) {
            throw new InvalidDomainObjectException("Reservation", "beds reserved must be positive");
        }
        if (totalPrice == null) {
            throw new InvalidDomainObjectException("Reservation", "total price cannot be null");
        }
        if (paid == null) {
            paid = false;
        }
        if (customers == null) {
            customers = Collections.emptyList();
        } else {
            customers = List.copyOf(customers);
        }
    }

    public long nights() {
        return dateRange.nights();
    }

    public boolean isPaid() {
        return paid != null && paid;
    }

    public Reservation markAsPaid() {
        return new Reservation(id, accommodationId, adminId, dateRange, bedsReserved,
                totalPrice, true, bookingDate, notes, customers);
    }

    public boolean hasCustomers() {
        return customers != null && !customers.isEmpty();
    }

    public int customerCount() {
        return customers != null ? customers.size() : 0;
    }

    public boolean overlaps(DateRange other) {
        return dateRange.overlaps(other);
    }
}