package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.application.port.out.reservation.*;
import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.DateRange;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.ReservationJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.mapper.ReservationMapper;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationPersistenceAdapter implements
        SaveReservationPort,
        LoadReservationPort,
        DeleteReservationPort,
        ListReservationsPort,
        FindOverlappingReservationsPort {

    private final ReservationJpaRepository repository;

    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        log.debug("Saving reservation to database for accommodation: {}", reservation.accommodationId());

        try {
            ReservationJpaEntity entity = ReservationMapper.toEntity(reservation);
            ReservationJpaEntity savedEntity = repository.save(entity);
            log.debug("Reservation saved successfully with id: {}", savedEntity.getId());

            return ReservationMapper.toDomain(savedEntity);
        } catch (Exception ex) {
            log.error("Error saving reservation: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to save reservation", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> loadById(Long id) {
        log.debug("Loading reservation by id: {}", id);

        return repository.findById(id)
                .map(ReservationMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting reservation by id: {}", id);

        try {
            repository.deleteById(id);
            log.debug("Reservation deleted successfully with id: {}", id);
        } catch (Exception ex) {
            log.error("Error deleting reservation: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to delete reservation", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        log.debug("Finding all reservations");

        return repository.findAll()
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByAccommodationId(Long accommodationId) {
        log.debug("Finding reservations by accommodation id: {}", accommodationId);

        return repository.findByAccommodationId(accommodationId)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByAdminId(Long adminId) {
        log.debug("Finding reservations by admin id: {}", adminId);

        return repository.findByAdminId(adminId)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding reservations by date range: {} to {}", startDate, endDate);

        return repository.findByDateRangeOverlap(startDate, endDate)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findUnpaid() {
        log.debug("Finding unpaid reservations");

        return repository.findByPaidFalse()
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findOverlapping(Long accommodationId, DateRange dateRange, Long excludeReservationId) {
        log.debug("Finding overlapping reservations for accommodation: {}", accommodationId);

        return repository.findOverlappingReservations(
                        accommodationId,
                        dateRange.startDate(),
                        dateRange.endDate(),
                        excludeReservationId
                )
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }
}