package com.aogdev.rural.application.port.out.customer;

import com.aogdev.rural.domain.model.Customer;

import java.util.List;

public interface ListCustomersPort {
    List<Customer> findAll();
    List<Customer> findByReservationId(Long reservationId);
    List<Customer> findPilgrims();
}