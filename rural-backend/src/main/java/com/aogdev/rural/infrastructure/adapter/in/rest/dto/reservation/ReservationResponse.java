package com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        Long accommodationId,
        String accommodationName,
        Long adminId,
        String adminFullName,
        LocalDate startDate,
        LocalDate endDate,
        Long nights,
        Integer bedsReserved,
        BigDecimal totalPrice,
        String currency,
        Boolean paid,
        LocalDate bookingDate,
        String notes
) {}