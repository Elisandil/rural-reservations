package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.application.port.out.customer.*;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.CustomerJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.mapper.CustomerMapper;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements
        SaveCustomerPort,
        LoadCustomerPort,
        FindCustomerByDniPort,
        DeleteCustomerPort,
        ListCustomersPort {

    private final CustomerJpaRepository repository;

    @Override
    @Transactional
    public Customer save(Customer customer) {
        log.debug("Saving customer to database: {}", customer.dni().value());

        try {
            CustomerJpaEntity entity = CustomerMapper.toEntity(customer);
            CustomerJpaEntity savedEntity = repository.save(entity);
            log.debug("Customer saved successfully with id: {}", savedEntity.getId());

            return CustomerMapper.toDomain(savedEntity);
        } catch (Exception ex) {
            log.error("Error saving customer: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to save customer", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> loadById(Long id) {
        log.debug("Loading customer by id: {}", id);

        return repository.findById(id)
                .map(CustomerMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByDni(DNI dni) {
        log.debug("Finding customer by DNI: {}", dni.value());

        return repository.findByDni(dni.value())
                .map(CustomerMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting customer by id: {}", id);

        try {
            repository.deleteById(id);
            log.debug("Customer deleted successfully with id: {}", id);
        } catch (Exception ex) {
            log.error("Error deleting customer: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to delete customer", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        log.debug("Finding all customers");

        return repository.findAll()
                .stream()
                .map(CustomerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findByReservationId(Long reservationId) {
        log.debug("Finding customers by reservation id: {}", reservationId);

        return repository.findByReservationId(reservationId)
                .stream()
                .map(CustomerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findPilgrims() {
        log.debug("Finding pilgrims");

        return repository.findByIsPilgrimTrue()
                .stream()
                .map(CustomerMapper::toDomain)
                .collect(Collectors.toList());
    }
}