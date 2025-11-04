package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.application.port.out.admin.FindAdminByEmailPort;
import com.aogdev.rural.application.port.out.admin.ListAdminsPort;
import com.aogdev.rural.application.port.out.admin.LoadAdminPort;
import com.aogdev.rural.application.port.out.admin.SaveAdminPort;
import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AdminJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.mapper.AdminMapper;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AdminJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPersistenceAdapter implements
        SaveAdminPort,
        LoadAdminPort,
        FindAdminByEmailPort,
        ListAdminsPort {

    private final AdminJpaRepository repository;

    @Override
    @Transactional
    public Admin save(Admin admin) {
        log.debug("Saving admin to database: {}", admin.email());

        try {
            AdminJpaEntity entity = AdminMapper.toEntity(admin);
            AdminJpaEntity savedEntity = repository.save(entity);
            log.debug("Admin saved successfully with id: {}", savedEntity.getId());

            return AdminMapper.toDomain(savedEntity);
        } catch (Exception ex) {
            log.error("Error saving admin: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to save admin", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Admin> loadById(Long id) {
        log.debug("Loading admin by id: {}", id);

        return repository.findById(id)
                .map(AdminMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Admin> findByEmail(Email email) {
        log.debug("Finding admin by email: {}", email);

        return repository.findByEmail(email.value())
                .map(AdminMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> findAll() {
        log.debug("Finding all admins");

        return repository.findAll()
                .stream()
                .map(AdminMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> findAllActive() {
        log.debug("Finding all active admins");

        return repository.findAllByActiveTrue()
                .stream()
                .map(AdminMapper::toDomain)
                .collect(Collectors.toList());
    }
}