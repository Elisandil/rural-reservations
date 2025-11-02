package com.aogdev.rural.domain.exception.admin;

import com.aogdev.rural.domain.exception.DomainException;
import com.aogdev.rural.domain.valueobject.Email;

public class AdminAlreadyExistsException extends DomainException {

    public AdminAlreadyExistsException(Email email) {
        super("Admin already exists with email: " + email.value());
    }
}
