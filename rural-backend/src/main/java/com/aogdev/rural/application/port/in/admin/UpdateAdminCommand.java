package com.aogdev.rural.application.command;

import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.domain.valueobjects.PersonName;
import com.aogdev.rural.domain.valueobjects.Phone;

public record UpdateAdminCommand(
        Long id,
        PersonName name,
        Email email,
        Phone phone
) {
    public UpdateAdminCommand {

        if (id == null) {
            throw new IllegalArgumentException("Admin ID cannot be null");
        }
    }
}