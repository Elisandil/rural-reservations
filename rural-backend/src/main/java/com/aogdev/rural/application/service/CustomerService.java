package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.customer.*;
import com.aogdev.rural.application.port.out.customer.*;
import com.aogdev.rural.application.port.out.reservation.LoadReservationPort;
import com.aogdev.rural.domain.exception.customer.CustomerAlreadyExistsException;
import com.aogdev.rural.domain.exception.customer.CustomerNotFoundException;
import com.aogdev.rural.domain.exception.reservation.ReservationNotFoundException;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomerService implements
        CreateCustomerUseCase,
        GetCustomerUseCase,
        FindCustomerUseCase,
        UpdateCustomerUseCase,
        DeleteCustomerUseCase,
        ListCustomersUseCase {

    private final SaveCustomerPort saveCustomerPort;
    private final LoadCustomerPort loadCustomerPort;
    private final FindCustomerByDniPort findCustomerByDniPort;
    private final DeleteCustomerPort deleteCustomerPort;
    private final ListCustomersPort listCustomersPort;
    private final LoadReservationPort loadReservationPort;

    @Override
    public Customer create(CreateCustomerCommand command) {
        log.info("Creating new customer with DNI: {}", command.dni().value());

        loadReservationPort.loadById(command.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException(command.reservationId()));

        findCustomerByDniPort.findByDni(command.dni())
                .ifPresent(existing -> {
                    throw new CustomerAlreadyExistsException(command.dni());
                });

        Customer customer = new Customer(
                null,
                command.reservationId(),
                command.name(),
                command.phone(),
                command.email(),
                command.nationality(),
                command.gender(),
                command.isPilgrim(),
                command.dni()
        );

        Customer savedCustomer = saveCustomerPort.save(customer);
        log.info("Customer created successfully with id: {}", savedCustomer.id());

        return savedCustomer;
    }

    @Override
    public Customer getById(Long id) {
        log.debug("Fetching customer with id: {}", id);

        return loadCustomerPort.loadById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    public Optional<Customer> findByDni(DNI dni) {
        log.debug("Finding customer by DNI: {}", dni.value());

        return findCustomerByDniPort.findByDni(dni);
    }

    @Override
    public Customer update(UpdateCustomerCommand command) {
        log.info("Updating customer with id: {}", command.id());

        Customer existingCustomer = loadCustomerPort.loadById(command.id())
                .orElseThrow(() -> new CustomerNotFoundException(command.id()));

        loadReservationPort.loadById(command.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException(command.reservationId()));

        if (!existingCustomer.dni().equals(command.dni())) {
            findCustomerByDniPort.findByDni(command.dni()).ifPresent(customer -> {
                if (!customer.id().equals(command.id())) {
                    throw new CustomerAlreadyExistsException(command.dni());
                }
            });
        }

        Customer updatedCustomer = new Customer(
                command.id(),
                command.reservationId(),
                command.name(),
                command.phone(),
                command.email(),
                command.nationality(),
                command.gender(),
                command.isPilgrim(),
                command.dni()
        );

        Customer savedCustomer = saveCustomerPort.save(updatedCustomer);
        log.info("Customer updated successfully with id: {}", savedCustomer.id());

        return savedCustomer;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting customer with id: {}", id);

        loadCustomerPort.loadById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        deleteCustomerPort.deleteById(id);
        log.info("Customer deleted successfully with id: {}", id);
    }

    @Override
    public List<Customer> listAll() {
        log.debug("Listing all customers");

        return listCustomersPort.findAll();
    }

    @Override
    public List<Customer> listByReservation(Long reservationId) {
        log.debug("Listing customers by reservation: {}", reservationId);

        loadReservationPort.loadById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        return listCustomersPort.findByReservationId(reservationId);
    }

    @Override
    public List<Customer> listPilgrims() {
        log.debug("Listing pilgrims");

        return listCustomersPort.findPilgrims();
    }
}