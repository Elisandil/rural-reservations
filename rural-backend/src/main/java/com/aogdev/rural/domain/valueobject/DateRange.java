package com.aogdev.rural.domain.valueobjects;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DateRange(LocalDate startDate, LocalDate endDate) {

    public DateRange {

        if (startDate == null || endDate == null) {
            throw new InvalidDomainObjectException("DateRange", "start date and end date cannot be null");
        }
        if (!endDate.isAfter(startDate)) {
            throw new InvalidDomainObjectException("DateRange", "end date must be after start date");
        }
    }

    public long nights() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public boolean overlaps(DateRange otherRange) {
        return !this.endDate.isBefore(otherRange.startDate) &&
                !otherRange.endDate.isBefore(this.startDate);
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(startDate) && date.isBefore(endDate);
    }
}