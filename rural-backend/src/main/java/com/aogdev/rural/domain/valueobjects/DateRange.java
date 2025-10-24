package com.aogdev.rural.domain.valueobjects;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DateRange(LocalDate startDate, LocalDate endDate) {

    public DateRange {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("The date range cannot be null");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("The end date must be after the start date");
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
