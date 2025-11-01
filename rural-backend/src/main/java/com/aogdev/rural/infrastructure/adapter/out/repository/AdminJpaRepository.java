package com.aogdev.rural.infrastructure.adapter.out.repository;

import com.aogdev.rural.infrastructure.adapter.out.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminJpaRepository  extends JpaRepository<AdminEntity, Long> {
    Optional<AdminEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<AdminEntity> findByActive(Boolean active);
}
