package com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation;

import java.math.BigDecimal;

public record AccommodationResponse(
        Long id,
        Short accommodationTypeId,
        String accommodationTypeName,
        String name,
        BigDecimal pricePerNight,
        String currency,
        Integer bedCapacity,
        Boolean active
) {}