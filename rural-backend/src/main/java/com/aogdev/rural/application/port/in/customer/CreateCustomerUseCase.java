package com.aogdev.rural.application.port.in.customer;

import com.aogdev.rural.domain.model.Customer;

public interface CreateCustomerUseCase {
    Customer create(CreateCustomerCommand command);
}