package com.aogdev.rural.application.port.in.customer;

import com.aogdev.rural.domain.model.Customer;

import java.util.List;

public interface ListCustomersUseCase {
    List<Customer> listAll();
    List<Customer> listByReservation(Long reservationId);
    List<Customer> listPilgrims();
}