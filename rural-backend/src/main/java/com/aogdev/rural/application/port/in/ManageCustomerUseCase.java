package com.aogdev.rural.application.port.in;

import com.aogdev.rural.application.command.CreateCustomerCommand;
import com.aogdev.rural.application.command.UpdateCustomerCommand;
import com.aogdev.rural.domain.model.Customer;

public interface ManageCustomerUseCase {
    Customer create(CreateCustomerCommand command);
    Customer update(UpdateCustomerCommand command);
    void delete(Long customerId);
}
