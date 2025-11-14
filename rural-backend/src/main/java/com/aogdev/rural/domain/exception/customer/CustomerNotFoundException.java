package com.aogdev.rural.domain.exception.customer;

import com.aogdev.rural.domain.exception.DomainException;

public class CustomerNotFoundException extends DomainException {

    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
}