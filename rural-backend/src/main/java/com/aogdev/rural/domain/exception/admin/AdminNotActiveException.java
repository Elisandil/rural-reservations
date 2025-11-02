package com.aogdev.rural.domain.exception.admin;

import com.aogdev.rural.domain.exception.DomainException;

public class AdminNotActiveException extends DomainException {

    public AdminNotActiveException(Long id) {
        super("Admin is not active with id: " + id);
    }
}
