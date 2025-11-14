package com.aogdev.rural.application.port.out.customer;

import com.aogdev.rural.domain.model.Customer;

import java.util.Optional;

public interface LoadCustomerPort {
    Optional<Customer> loadById(Long id);
}