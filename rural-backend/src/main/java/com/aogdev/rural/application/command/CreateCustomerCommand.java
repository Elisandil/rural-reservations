package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.DNI;
import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.domain.valueobjects.PersonName;
import com.aogdev.rural.domain.valueobjects.Phone;

public record CreateCustomerCommand(
        PersonName name,
        Phone phone,
        Email email,
        String nationality,
        Gender gender,
        Boolean isPilgrim,
        DNI dni
) {

    public CreateCustomerCommand {

        if (name == null) {
            throw new InvalidDomainObjectException("CreateCustomerCommand", "name cannot be null");
        }
        if (gender == null) {
            throw new InvalidDomainObjectException("CreateCustomerCommand", "gender cannot be null");
        }
        if (nationality == null || nationality.isBlank()) {
            throw new InvalidDomainObjectException("CreateCustomerCommand", "nationality cannot be empty");
        }
        if (dni == null) {
            throw new InvalidDomainObjectException("CreateCustomerCommand", "DNI cannot be null");
        }
    }
}