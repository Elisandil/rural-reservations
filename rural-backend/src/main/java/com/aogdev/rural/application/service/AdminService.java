package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.admin.*;
import com.aogdev.rural.application.port.out.admin.*;
import com.aogdev.rural.domain.exception.admin.AdminAlreadyExistsException;
import com.aogdev.rural.domain.exception.admin.AdminNotActiveException;
import com.aogdev.rural.domain.exception.admin.AdminNotFoundException;
import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService implements
        CreateAdminUseCase,
        GetAdminUseCase,
        FindAdminUseCase,
        UpdateAdminUseCase,
        ActivateAdminUseCase,
        DeactivateAdminUseCase,
        ChangeAdminPasswordUseCase,
        ListAdminsUseCase {

    private final SaveAdminPort saveAdminPort;
    private final LoadAdminPort loadAdminPort;
    private final FindAdminByEmailPort findAdminByEmailPort;
    private final ListAdminsPort listAdminsPort;
    private final HashPasswordPort hashPasswordPort;

    @Override
    public Admin create(CreateAdminCommand command) {
        log.info("Creating new admin with email: {}", command.email());

        findAdminByEmailPort.findByEmail(command.email())
                .ifPresent(existing -> {
                    throw new AdminAlreadyExistsException(command.email());
                });

        String hashedPassword = hashPasswordPort.hash(command.password());
        LocalDateTime now = LocalDateTime.now();

        Admin admin = new Admin(
                null,
                command.name(),
                command.email(),
                command.phone(),
                hashedPassword,
                true,
                now,
                now
        );
        Admin savedAdmin = saveAdminPort.save(admin);
        log.info("Admin created successfully with id: {}", savedAdmin.id());

        return savedAdmin;
    }

    @Override
    public Admin getById(Long id) {
        log.debug("Fetching admin with id: {}", id);

        return loadAdminPort.loadById(id)
                .orElseThrow(() -> new AdminNotFoundException(id));
    }

    @Override
    public Optional<Admin> findByEmail(Email email) {
        log.debug("Finding admin by email: {}", email);

        return findAdminByEmailPort.findByEmail(email);
    }

    @Override
    public Admin update(UpdateAdminCommand command) {
        log.info("Updating admin with id: {}", command.id());

        Admin existingAdmin = loadAdminPort.loadById(command.id())
                .orElseThrow(() -> new AdminNotFoundException(command.id()));

        if (!existingAdmin.isActive()) {
            log.warn("Attempted to update inactive admin with id: {}", command.id());
            throw new AdminNotActiveException(command.id());
        }

        if (!existingAdmin.email().equals(command.email())) {
            findAdminByEmailPort.findByEmail(command.email()).ifPresent(admin -> {

                if (!admin.id().equals(command.id())) {
                    throw new AdminAlreadyExistsException(command.email());
                }
            });
        }
        Admin updatedAdmin = new Admin(
                existingAdmin.id(),
                command.name(),
                command.email(),
                command.phone(),
                existingAdmin.passwordHash(),
                existingAdmin.active(),
                existingAdmin.createdAt(),
                LocalDateTime.now()
        );
        Admin savedAdmin = saveAdminPort.save(updatedAdmin);
        log.info("Admin updated successfully with id: {}", savedAdmin.id());

        return savedAdmin;
    }

    @Override
    public void activate(Long id) {
        log.info("Activating admin with id: {}", id);

        Admin admin = loadAdminPort.loadById(id)
                .orElseThrow(() -> new AdminNotFoundException(id));

        if (!admin.isActive()) {
            Admin activatedAdmin = admin.activate();
            saveAdminPort.save(activatedAdmin);

            log.info("Admin activated successfully with id: {}", id);
        } else {
            log.debug("Admin already active with id: {}", id);
        }
    }

    @Override
    public void deactivate(Long id) {
        log.info("Deactivating admin with id: {}", id);

        Admin admin = loadAdminPort.loadById(id)
                .orElseThrow(() -> new AdminNotFoundException(id));

        if (admin.isActive()) {
            Admin deactivatedAdmin = admin.deactivate();
            saveAdminPort.save(deactivatedAdmin);

            log.info("Admin deactivated successfully with id: {}", id);
        } else {
            log.debug("Admin already inactive with id: {}", id);
        }
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        log.info("Changing password for admin with id: {}", id);

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        Admin admin = loadAdminPort.loadById(id)
                .orElseThrow(() -> new AdminNotFoundException(id));

        if (!admin.isActive()) {
            log.warn("Attempted to change password for inactive admin with id: {}", id);
            throw new AdminNotActiveException(id);
        }
        String newHashedPassword = hashPasswordPort.hash(newPassword);

        Admin updatedAdmin = new Admin(
                admin.id(),
                admin.name(),
                admin.email(),
                admin.phone(),
                newHashedPassword,
                admin.active(),
                admin.createdAt(),
                LocalDateTime.now()
        );
        saveAdminPort.save(updatedAdmin);

        log.info("Password changed successfully for admin with id: {}", id);
    }

    @Override
    public List<Admin> listAll() {
        log.debug("Listing all admins");

        return listAdminsPort.findAll();
    }

    @Override
    public List<Admin> listActive() {
        log.debug("Listing active admins");

        return listAdminsPort.findAllActive();
    }
}
