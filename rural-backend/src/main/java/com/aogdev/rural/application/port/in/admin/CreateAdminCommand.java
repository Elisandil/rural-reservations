package com.aogdev.rural.application.command;

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
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}