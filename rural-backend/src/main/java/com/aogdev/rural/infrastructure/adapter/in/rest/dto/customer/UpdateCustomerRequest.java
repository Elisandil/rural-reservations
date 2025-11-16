package com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(

        @NotNull(message = "Reservation ID is required")
        Long reservationId,

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Surnames are required")
        @Size(max = 200, message = "Surnames must not exceed 200 characters")
        String surnames,

        @NotBlank(message = "Phone is required")
        String phone,

        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Nationality is required")
        @Size(max = 100, message = "Nationality must not exceed 100 characters")
        String nationality,

        @NotNull(message = "Gender is required")
        @Pattern(regexp = "M|F|O", message = "Gender must be M, F, or O")
        String gender,

        Boolean isPilgrim,

        @NotBlank(message = "DNI is required")
        @Pattern(regexp = "^[0-9]{8}[A-Z]$|^[XYZ][0-9]{7}[A-Z]$",
                message = "DNI must be valid (8 digits + letter or NIE format)")
        String dni
) {}