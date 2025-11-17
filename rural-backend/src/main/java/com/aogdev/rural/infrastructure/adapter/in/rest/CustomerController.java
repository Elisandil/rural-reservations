package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.application.port.in.customer.*;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.CreateCustomerRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.CustomerResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.UpdateCustomerRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.mapper.CustomerRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final FindCustomerUseCase findCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        log.info("REST request to create customer with DNI: {}", request.dni());

        CreateCustomerCommand command = CustomerRestMapper.toCommand(request);
        Customer customer = createCustomerUseCase.create(command);
        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        log.info("REST request to get customer by id: {}", id);

        Customer customer = getCustomerUseCase.getById(id);
        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<CustomerResponse> getCustomerByDni(@PathVariable String dni) {
        log.info("REST request to get customer by DNI: {}", dni);

        Optional<Customer> customerOpt = findCustomerUseCase.findByDni(new DNI(dni));

        return customerOpt
                .map(CustomerRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> listCustomers(
            @RequestParam(required = false) Long reservationId,
            @RequestParam(required = false, defaultValue = "false") boolean pilgrimsOnly) {

        log.info("REST request to list customers (reservationId: {}, pilgrimsOnly: {})",
                reservationId, pilgrimsOnly);

        List<Customer> customers;

        if (reservationId != null) {
            customers = listCustomersUseCase.listByReservation(reservationId);
        } else if (pilgrimsOnly) {
            customers = listCustomersUseCase.listPilgrims();
        } else {
            customers = listCustomersUseCase.listAll();
        }

        List<CustomerResponse> response = CustomerRestMapper.toResponseList(customers);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        log.info("REST request to update customer with id: {}", id);

        UpdateCustomerCommand command = CustomerRestMapper.toCommand(id, request);
        Customer customer = updateCustomerUseCase.update(command);
        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("REST request to delete customer with id: {}", id);

        deleteCustomerUseCase.delete(id);

        return ResponseEntity.noContent().build();
    }
}