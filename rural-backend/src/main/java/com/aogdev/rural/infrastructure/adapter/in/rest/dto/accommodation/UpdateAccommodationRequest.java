package com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateAccommodationRequest(

        @NotNull(message = "Accommodation type ID is required")
        Short accommodationTypeId,

        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must not exceed 200 characters")
        String name,

        @NotNull(message = "Price per night is required")
        @Positive(message = "Price per night must be positive")
        BigDecimal pricePerNight,

        @NotNull(message = "Bed capacity is required")
        @Min(value = 1, message = "Bed capacity must be at least 1")
        Integer bedCapacity
) {}