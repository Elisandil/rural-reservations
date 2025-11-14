package com.aogdev.rural.application.port.out.customer;

import com.aogdev.rural.domain.model.Customer;

public interface SaveCustomerPort {
    Customer save(Customer customer);
}