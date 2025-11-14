package com.aogdev.rural.domain.exception.customer;

import com.aogdev.rural.domain.exception.DomainException;
import com.aogdev.rural.domain.valueobject.DNI;

public class CustomerAlreadyExistsException extends DomainException {

    public CustomerAlreadyExistsException(DNI dni) {
        super("Customer already exists with DNI: " + dni.value());
    }
}