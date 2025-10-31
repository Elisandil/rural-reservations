package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.DNI;
import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.domain.valueobjects.PersonName;
import com.aogdev.rural.domain.valueobjects.Phone;

public record UpdateCustomerCommand(
        Long customerId,
        PersonName name,
        Phone phone,
        Email email,
        String nationality,
        Gender gender,
        Boolean isPilgrim,
        DNI dni
) {

    public UpdateCustomerCommand {

        if (customerId == null) {
            throw new InvalidDomainObjectException("UpdateCustomerCommand", "customer ID cannot be null");
        }
    }
}