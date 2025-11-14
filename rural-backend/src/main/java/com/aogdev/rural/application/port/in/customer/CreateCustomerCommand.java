package com.aogdev.rural.application.port.in.customer;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;

public record CreateCustomerCommand(
        Long reservationId,
        PersonName name,
        Phone phone,
        Email email,
        String nationality,
        Gender gender,
        Boolean isPilgrim,
        DNI dni
) {
    public CreateCustomerCommand {

        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (nationality == null || nationality.isBlank()) {
            throw new IllegalArgumentException("Nationality cannot be null or blank");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        if (dni == null) {
            throw new IllegalArgumentException("DNI cannot be null");
        }
    }
}