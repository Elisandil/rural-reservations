package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.domain.valueobjects.PersonName;
import com.aogdev.rural.domain.valueobjects.Phone;

public record UpdateAdminCommand(
        Long adminId,
        PersonName name,
        Email email,
        Phone phone
) {

    public UpdateAdminCommand {

        if (adminId == null) {
            throw new InvalidDomainObjectException("UpdateAdminCommand", "admin ID cannot be null");
        }
    }
}