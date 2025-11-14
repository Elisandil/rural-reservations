package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;

public record Customer(
        Long id,
        Long reservationId,
        PersonName name,
        Phone phone,
        Email email,
        String nationality,
        Gender gender,
        Boolean isPilgrim,
        DNI dni
) {

    public Customer {

        if (reservationId == null) {
            throw new InvalidDomainObjectException("Customer", "reservation ID cannot be null");
        }
        if (nationality == null || nationality.isBlank()) {
            throw new InvalidDomainObjectException("Customer", "nationality cannot be empty");
        }
        if (gender == null) {
            throw new InvalidDomainObjectException("Customer", "gender cannot be null");
        }
        if (isPilgrim == null) {
            isPilgrim = false;
        }
    }

    public String fullName() {
        return name.fullName();
    }

    public Boolean isPilgrim() {
        return isPilgrim != null && isPilgrim;
    }
}