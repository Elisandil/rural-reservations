package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.Customer;

public interface SaveCustomerPort {
    Customer save(Customer customer);
    void delete(Long customerId);
}
