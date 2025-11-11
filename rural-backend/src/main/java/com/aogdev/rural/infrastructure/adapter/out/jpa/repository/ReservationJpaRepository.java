package com.aogdev.rural.infrastructure.adapter.out.jpa.repository;

import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.ReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
    List<ReservationJpaEntity> findByAccommodationId(Long accommodationId);
    List<ReservationJpaEntity> findByAdminId(Long adminId);
    List<ReservationJpaEntity> findByPaidFalse();

    @Query("SELECT r FROM ReservationJpaEntity r WHERE " +
            "r.startDate <= :endDate AND r.endDate >= :startDate")
    List<ReservationJpaEntity> findByDateRangeOverlap(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM ReservationJpaEntity r WHERE " +
            "r.accommodationId = :accommodationId AND " +
            "r.startDate < :endDate AND r.endDate > :startDate AND " +
            "(:excludeId IS NULL OR r.id <> :excludeId)")
    List<ReservationJpaEntity> findOverlappingReservations(
            @Param("accommodationId") Long accommodationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );
}