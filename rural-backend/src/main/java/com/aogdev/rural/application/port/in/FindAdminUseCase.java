package com.aogdev.rural.application.port.in;

import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobjects.Email;

import java.util.List;
import java.util.Optional;

public interface FindAdminUseCase {
    Optional<Admin> findById(Long id);
    Optional<Admin> findByEmail(Email email);
    List<Admin> findAll();
    List<Admin> findActive();
}
