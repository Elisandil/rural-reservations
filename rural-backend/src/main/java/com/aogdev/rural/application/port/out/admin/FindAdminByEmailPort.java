package com.aogdev.rural.application.port.out.admin;

import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;

import java.util.Optional;

public interface FindAdminByEmailPort {
    Optional<Admin> findByEmail(Email email);
}
