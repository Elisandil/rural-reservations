package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.application.port.in.customer.CreateCustomerCommand;
import com.aogdev.rural.application.port.in.customer.UpdateCustomerCommand;
import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.CreateCustomerRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.CustomerResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.UpdateCustomerRequest;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerRestMapper {

    public static CreateCustomerCommand toCommand(CreateCustomerRequest request) {

        return new CreateCustomerCommand(
                request.reservationId(),
                new PersonName(request.firstName(), request.surnames()),
                new Phone(request.phone()),
                request.email() != null ? new Email(request.email()) : null,
                request.nationality(),
                Gender.fromCode(request.gender().charAt(0)),
                request.isPilgrim() != null ? request.isPilgrim() : false,
                new DNI(request.dni())
        );
    }

    public static UpdateCustomerCommand toCommand(Long id, UpdateCustomerRequest request) {

        return new UpdateCustomerCommand(
                id,
                request.reservationId(),
                new PersonName(request.firstName(), request.surnames()),
                new Phone(request.phone()),
                request.email() != null ? new Email(request.email()) : null,
                request.nationality(),
                Gender.fromCode(request.gender().charAt(0)),
                request.isPilgrim() != null ? request.isPilgrim() : false,
                new DNI(request.dni())
        );
    }

    public static CustomerResponse toResponse(Customer customer) {

        return new CustomerResponse(
                customer.id(),
                customer.reservationId(),
                customer.name().firstName(),
                customer.name().surnames(),
                customer.fullName(),
                customer.phone() != null ? customer.phone().value() : null,
                customer.phone() != null ? customer.phone().formatted() : null,
                customer.email() != null ? customer.email().value() : null,
                customer.nationality(),
                customer.gender().getCode(),
                customer.isPilgrim(),
                customer.dni().value()
        );
    }

    public static List<CustomerResponse> toResponseList(List<Customer> customers) {

        return customers.stream()
                .map(CustomerRestMapper::toResponse)
                .collect(Collectors.toList());
    }
}