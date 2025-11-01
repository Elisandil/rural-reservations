package com.aogdev.rural.application.service.admin;

import com.aogdev.rural.application.command.CreateAdminCommand;
import com.aogdev.rural.application.command.UpdateAdminCommand;
import com.aogdev.rural.application.port.in.ManageAdminUseCase;
import com.aogdev.rural.application.port.out.HashPasswordPort;
import com.aogdev.rural.application.port.out.LoadAdminPort;
import com.aogdev.rural.application.port.out.SaveAdminPort;
import com.aogdev.rural.domain.exception.DomainException;
import com.aogdev.rural.domain.exception.EntityNotFoundException;
import com.aogdev.rural.domain.model.Admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ManageAdminService implements ManageAdminUseCase {
    private final LoadAdminPort loadAdminPort;
    private final SaveAdminPort saveAdminPort;
    private final HashPasswordPort hashPasswordPort;

    @Override
    public Admin create(CreateAdminCommand command) {
        log.debug("Creating new admin with Email: {}", command.email().value());

        if (loadAdminPort.existsByEmail(command.email())) {
            throw new DomainException("Admin with Email: " + command.email().value() + " already exists");
        }

        String hashedPassword = hashPasswordPort.hash(command.password());
        Admin admin = new Admin(
                null,
                command.name(),
                command.email(),
                command.phone(),
                hashedPassword,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Admin savedAdmin = saveAdminPort.save(admin);
        log.info("Admin created successfully with ID: {}", savedAdmin.id());

        return savedAdmin;
    }

    public Admin update(UpdateAdminCommand command) {
        log.debug("Updating admin with ID: {}", command.adminId());

        Admin existingAdmin = loadAdminPort.loadById(command.adminId()).orElseThrow(() ->
                new EntityNotFoundException("Admin", command.adminId()));

        if (command.email() != null &&
                !command.email().equals(existingAdmin.email()) &&
                loadAdminPort.existsByEmail(command.email())) {

            throw new DomainException("Email " + command.email().value() + " is already in use");
        }

        Admin updatedAdmin = new Admin(
                existingAdmin.id(),
                command.name() != null ? command.name() : existingAdmin.name(),
                command.email() != null ? command.email() : existingAdmin.email(),
                command.phone() != null ? command.phone() : existingAdmin.phone(),
                existingAdmin.passwordHash(),
                existingAdmin.active(),
                existingAdmin.createdAt(),
                LocalDateTime.now()
        );
        Admin savedAdmin = saveAdminPort.save(updatedAdmin);
        log.info("Admin updated successfully with ID: {}", savedAdmin.id());

        return savedAdmin;
    }

    @Override
    public void activate(Long adminId) {
        log.debug("Activating admin with ID: {}", adminId);

        Admin admin = loadAdminPort.loadById(adminId).orElseThrow(() ->
                new EntityNotFoundException("Admin", adminId));

        if (admin.isActive()) {
            log.info("Admin with ID: {} is already active", adminId);
            return ;
        }

        Admin activatedAdmin = admin.activate();
        saveAdminPort.save(activatedAdmin);
        log.info("Admin activated successfully with ID: {}", adminId);
    }

    @Override
    public void deactivate(Long adminId) {
        log.debug("Deactivating admin with ID: {}", adminId);

        Admin admin = loadAdminPort.loadById(adminId).orElseThrow(() ->
                new EntityNotFoundException("Admin", adminId));

        if (!admin.isActive()) {
            log.info("Admin with ID: {} is inactive", adminId);
            return ;
        }

        Admin deactivatedAdmin = admin.deactivate();
        saveAdminPort.save(deactivatedAdmin);
        log.info("Admin deactivated successfully with ID: {}", adminId);
    }

    @Override
    public void changePassword(Long adminId, String newPassword) {
        log.debug("Changing password for admin with ID: {}", adminId);

        Admin admin = loadAdminPort.loadById(adminId).orElseThrow(() ->
                new EntityNotFoundException("Admin", adminId));

        String hashedPassword = hashPasswordPort.hash(newPassword);

        Admin updatedAdmin = new Admin(
                admin.id(),
                admin.name(),
                admin.email(),
                admin.phone(),
                hashedPassword,
                admin.active(),
                admin.createdAt(),
                LocalDateTime.now()
        );
        saveAdminPort.save(updatedAdmin);
        log.info("Password changed successfully for admin with ID: {}", adminId);
    }
}
