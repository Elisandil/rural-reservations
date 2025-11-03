package com.aogdev.rural.application.port.in.admin;

import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;

import java.util.Optional;

public interface FindAdminUseCase {
    Optional<Admin> findByEmail(Email email);
}
