package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.DNI;
import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.domain.valueobjects.PersonName;
import com.aogdev.rural.domain.valueobjects.Phone;

public record Customer(
        Long id,
        PersonName name,
        Phone phone,
        Email email,
        String nationality,
        Gender gender,
        Boolean isPilgrim,
        DNI dni
) {

    public Customer {
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