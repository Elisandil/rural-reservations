package com.aogdev.rural.application.port.in.customer;

import com.aogdev.rural.domain.model.Customer;

public interface GetCustomerUseCase {
    Customer getById(Long id);
}