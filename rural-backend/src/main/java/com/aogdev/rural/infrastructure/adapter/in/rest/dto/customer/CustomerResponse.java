package com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer;

public record CustomerResponse(
        Long id,
        Long reservationId,
        String firstName,
        String surnames,
        String fullName,
        String phone,
        String phoneFormatted,
        String email,
        String nationality,
        char gender,
        Boolean isPilgrim,
        String dni
) {}