package com.aogdev.rural.application.port.in.admin;

import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;

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