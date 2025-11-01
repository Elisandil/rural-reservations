package com.aogdev.rural.application.service.admin;

import com.aogdev.rural.application.port.in.FindAdminUseCase;
import com.aogdev.rural.application.port.out.LoadAdminPort;
import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobjects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindAdminService implements FindAdminUseCase {
    private final LoadAdminPort loadAdminPort;

    @Override
    public Optional<Admin> findById(Long id) {
        log.debug("Finding admin by ID: {}", id);
        return loadAdminPort.loadById(id);
    }

    @Override
    public Optional<Admin> findByEmail(Email email) {
        log.debug("Finding admin by Email: {}", email.value());
        return loadAdminPort.loadByEmail(email);
    }

    @Override
    public List<Admin> findAll() {
        log.debug("Finding all admins");
        return loadAdminPort.loadAll();
    }

    @Override
    public List<Admin> findActive() {
        log.debug("Finding all active admins");
        return loadAdminPort.loadActive();
    }
}
