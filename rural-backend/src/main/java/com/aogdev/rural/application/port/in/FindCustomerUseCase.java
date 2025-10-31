package com.aogdev.rural.application.port.in;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobjects.DNI;
import com.aogdev.rural.domain.valueobjects.Email;

import java.util.List;
import java.util.Optional;

public interface FindCustomerUseCase {
    Optional<Customer> findById(Long id);
    Optional<Customer> findByDNI(DNI dni);
    Optional<Customer> findByEmail(Email email);
    List<Customer> findAll();
    List<Customer> findPilgrims();
    List<Customer> findByNationality(String nationality);
    List<Customer> findByGender(Gender gender);
}
