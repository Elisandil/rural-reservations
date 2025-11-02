package com.aogdev.rural.domain.exception.admin;

import com.aogdev.rural.domain.exception.DomainException;

public class AdminNotFoundException extends DomainException {

    public AdminNotFoundException(Long id) {
        super("Admin not found with id: " + id);
    }
}
