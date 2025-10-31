package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobjects.Email;

import java.util.List;
import java.util.Optional;

public interface LoadAdminPort {
    Optional<Admin> loadById(Long id);
    Optional<Admin> loadByEmail(Email email);
    List<Admin> loadAll();
    List<Admin> loadActive();
    Boolean existsById(Long id);
    Boolean existsByEmail(Email email);
}
