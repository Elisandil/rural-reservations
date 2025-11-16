package com.aogdev.rural.infrastructure.adapter.out.jpa.repository;

import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Long> {
    Optional<CustomerJpaEntity> findByDni(String dni);
    List<CustomerJpaEntity> findByReservationId(Long reservationId);
    List<CustomerJpaEntity> findByIsPilgrimTrue();
    boolean existsByDni(String dni);
}