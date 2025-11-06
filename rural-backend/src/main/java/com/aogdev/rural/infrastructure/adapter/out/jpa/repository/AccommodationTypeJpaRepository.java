package com.aogdev.rural.infrastructure.adapter.out.jpa.repository;

import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AccommodationTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccommodationTypeJpaRepository extends JpaRepository<AccommodationTypeJpaEntity, Short> {
    Optional<AccommodationTypeJpaEntity> findByName(String name);
    boolean existsByName(String name);
}