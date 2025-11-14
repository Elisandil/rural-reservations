package com.aogdev.rural.application.port.out.customer;

import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;

import java.util.Optional;

public interface FindCustomerByDniPort {
    Optional<Customer> findByDni(DNI dni);
}