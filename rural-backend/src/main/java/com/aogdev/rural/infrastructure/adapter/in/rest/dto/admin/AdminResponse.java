package com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin;

import java.time.LocalDateTime;

public record AdminResponse(
        Long id,
        String firstName,
        String surnames,
        String fullName,
        String email,
        String phone,
        String phoneFormatted,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
