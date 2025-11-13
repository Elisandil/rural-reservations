package com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReservationRequest(

        @NotNull(message = "Accommodation ID is required")
        Long accommodationId,

        @NotNull(message = "Admin ID is required")
        Long adminId,

        @NotNull(message = "Start date is required")
        @Future(message = "Start date must be in the future")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        LocalDate endDate,

        @NotNull(message = "Beds reserved is required")
        @Min(value = 1, message = "Beds reserved must be at least 1")
        Integer bedsReserved,

        String notes
) {}