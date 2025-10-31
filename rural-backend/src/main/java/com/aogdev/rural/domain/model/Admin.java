package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.domain.valueobjects.PersonName;
import com.aogdev.rural.domain.valueobjects.Phone;

import java.time.LocalDateTime;

public record Admin(
        Long id,
        PersonName name,
        Email email,
        Phone phone,
        String passwordHash,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public Admin {

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new InvalidDomainObjectException("Admin", "password hash cannot be empty");
        }
        if (active == null) {
            active = true;
        }
    }

    public Admin activate() {
        return new Admin(id, name, email, phone, passwordHash, true, createdAt, updatedAt);
    }

    public Admin deactivate() {
        return new Admin(id, name, email, phone, passwordHash, false, createdAt, updatedAt);
    }

    public boolean isActive() {
        return active != null && active;
    }
}