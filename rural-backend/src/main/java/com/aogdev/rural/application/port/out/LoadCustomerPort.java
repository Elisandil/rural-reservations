package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobjects.DNI;
import com.aogdev.rural.domain.valueobjects.Email;

import java.util.List;
import java.util.Optional;

public interface LoadCustomerPort {
    Optional<Customer> loadById(Long id);
    Optional<Customer> loadByEmail(Email email);
    Optional<Customer> loadByDNI(DNI dni);
    List<Customer> loadAll();
    List<Customer> loadPilgrims();
    List<Customer> loadByNationality(String nationality);
    List<Customer> loadByGender(Gender gender);
    Boolean existsById(Long id);
    Boolean existsByDNI(DNI dni);
}
