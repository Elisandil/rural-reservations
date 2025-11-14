package com.aogdev.rural.application.port.in.customer;

import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;

import java.util.Optional;

public interface FindCustomerUseCase {
    Optional<Customer> findByDni(DNI dni);
}