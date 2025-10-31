package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.domain.valueobjects.PersonName;
import com.aogdev.rural.domain.valueobjects.Phone;

public record CreateAdminCommand(
        PersonName name,
        Email email,
        Phone phone,
        String password
) {

    public CreateAdminCommand {

        if (name == null) {
            throw new InvalidDomainObjectException("CreateAdminCommand", "name cannot be null");
        }
        if (email == null) {
            throw new InvalidDomainObjectException("CreateAdminCommand", "email cannot be null");
        }
        if (phone == null) {
            throw new InvalidDomainObjectException("CreateAdminCommand", "phone cannot be null");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidDomainObjectException("CreateAdminCommand", "password cannot be empty");
        }
    }
}