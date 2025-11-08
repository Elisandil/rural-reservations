package com.aogdev.rural.infrastructure.adapter.out.jpa.repository;

import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AccommodationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationJpaRepository extends JpaRepository<AccommodationJpaEntity, Long> {
    Optional<AccommodationJpaEntity> findByName(String name);
    List<AccommodationJpaEntity> findAllByActiveTrue();
    List<AccommodationJpaEntity> findAllByAccommodationTypeId(Short accommodationTypeId);
    boolean existsByName(String name);
}