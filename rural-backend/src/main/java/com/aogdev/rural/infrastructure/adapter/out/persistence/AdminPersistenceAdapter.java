package com.aogdev.rural.infrastructure.adapter.out.persistence;

import com.aogdev.rural.application.port.out.LoadAdminPort;
import com.aogdev.rural.application.port.out.SaveAdminPort;
import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.infrastructure.adapter.out.entity.AdminEntity;
import com.aogdev.rural.infrastructure.adapter.out.mapper.AdminMapper;
import com.aogdev.rural.infrastructure.adapter.out.repository.AdminJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminPersistenceAdapter implements LoadAdminPort, SaveAdminPort {
    private final AdminJpaRepository repository;
    private final AdminMapper mapper;

    @Override
    public Optional<Admin> loadById(Long id) {
        log.debug("Loading admin by ID: {}", id);
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Admin> loadByEmail(Email email) {
        log.debug("Loading admin by Email: {}", email.value());
        return repository.findByEmail(email.value()).map(mapper::toDomain);
    }

    @Override
    public List<Admin> loadAll() {
        log.debug("Loading all admins");
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Admin> loadActive() {
        log.debug("Loading active admins");
        return repository.findByActive(true).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Boolean existsById(Long id) {
        log.debug("Checking if admin exists by ID: {}", id);
        return repository.existsById(id);
    }

    @Override
    public Boolean existsByEmail(Email email) {
        log.debug("Checking if admin exists by email: {}", email.value());
        return repository.existsByEmail(email.value());
    }

    @Override
    public Admin save(Admin admin) {
        log.debug("Saving admin with ID: {}", admin.id());
        AdminEntity entity = mapper.toEntity(admin);
        AdminEntity saved = repository.save(entity);
        log.info("Admin saved successfully with ID: {}", saved.getId());
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Long adminId) {
        log.debug("Deleting admin with ID: {}", adminId);
        repository.deleteById(adminId);
        log.info("Admin deleted successfully with ID: {}", adminId);
    }
}
