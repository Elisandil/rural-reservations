package com.aogdev.rural.infrastructure.adapter.out.jpa.repository;

import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AdminJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminJpaRepository extends JpaRepository<AdminJpaEntity, Long> {
    Optional<AdminJpaEntity> findByEmail(String email);
    List<AdminJpaEntity> findAllByActiveTrue();
    boolean existsByEmail(String email);
}
