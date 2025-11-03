package com.aogdev.rural.application.port.out.admin;

import com.aogdev.rural.domain.model.Admin;

import java.util.Optional;

public interface LoadAdminPort {
    Optional<Admin> loadById(Long id);
}
