package com.aogdev.rural.application.port.in.admin;

import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;

public record CreateAdminCommand(
        PersonName name,
        Email email,
        Phone phone,
        String password
) {
    public CreateAdminCommand {

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}