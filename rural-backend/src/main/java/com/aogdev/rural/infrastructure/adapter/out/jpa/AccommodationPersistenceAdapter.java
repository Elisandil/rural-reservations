package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.application.port.out.accommodation.*;
import com.aogdev.rural.application.port.out.accommodationType.LoadAccommodationTypePort;
import com.aogdev.rural.domain.exception.accommodationType.AccommodationTypeNotFoundException;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AccommodationJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.mapper.AccommodationMapper;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationJpaRepository;
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
public class AccommodationPersistenceAdapter implements
        SaveAccommodationPort,
        LoadAccommodationPort,
        FindAccommodationByNamePort,
        ListAccommodationsPort {

    private final AccommodationJpaRepository repository;
    private final LoadAccommodationTypePort loadAccommodationTypePort;

    @Override
    @Transactional
    public Accommodation save(Accommodation accommodation) {
        log.debug("Saving accommodation to database: {}", accommodation.name());

        try {
            AccommodationJpaEntity entity = AccommodationMapper.toEntity(accommodation);
            AccommodationJpaEntity savedEntity = repository.save(entity);
            log.debug("Accommodation saved successfully with id: {}", savedEntity.getId());

            AccommodationType accommodationType = loadAccommodationTypePort
                    .loadById(savedEntity.getAccommodationTypeId())
                    .orElseThrow(() -> new AccommodationTypeNotFoundException(savedEntity
                            .getAccommodationTypeId()));

            return AccommodationMapper.toDomain(savedEntity, accommodationType);
        } catch (Exception ex) {
            log.error("Error saving accommodation: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to save accommodation", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Accommodation> loadById(Long id) {
        log.debug("Loading accommodation by id: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    AccommodationType accommodationType = loadAccommodationTypePort
                            .loadById(entity.getAccommodationTypeId())
                            .orElseThrow(() -> new AccommodationTypeNotFoundException(entity
                                    .getAccommodationTypeId()));

                    return AccommodationMapper.toDomain(entity, accommodationType);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Accommodation> findByName(String name) {
        log.debug("Finding accommodation by name: {}", name);

        return repository.findByName(name)
                .map(entity -> {
                    AccommodationType accommodationType = loadAccommodationTypePort
                            .loadById(entity.getAccommodationTypeId())
                            .orElseThrow(() -> new AccommodationTypeNotFoundException(entity
                                    .getAccommodationTypeId()));

                    return AccommodationMapper.toDomain(entity, accommodationType);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Accommodation> findAll() {
        log.debug("Finding all accommodations");

        return repository.findAll()
                .stream()
                .map(entity -> {
                    AccommodationType accommodationType = loadAccommodationTypePort
                            .loadById(entity.getAccommodationTypeId())
                            .orElseThrow(() -> new AccommodationTypeNotFoundException(entity
                                    .getAccommodationTypeId()));

                    return AccommodationMapper.toDomain(entity, accommodationType);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Accommodation> findAllActive() {
        log.debug("Finding all active accommodations");

        return repository.findAllByActiveTrue()
                .stream()
                .map(entity -> {
                    AccommodationType accommodationType = loadAccommodationTypePort
                            .loadById(entity.getAccommodationTypeId())
                            .orElseThrow(() -> new AccommodationTypeNotFoundException(entity
                                    .getAccommodationTypeId()));

                    return AccommodationMapper.toDomain(entity, accommodationType);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Accommodation> findByAccommodationTypeId(Short accommodationTypeId) {
        log.debug("Finding accommodations by type id: {}", accommodationTypeId);

        return repository.findAllByAccommodationTypeId(accommodationTypeId)
                .stream()
                .map(entity -> {
                    AccommodationType accommodationType = loadAccommodationTypePort
                            .loadById(entity.getAccommodationTypeId())
                            .orElseThrow(() -> new AccommodationTypeNotFoundException(entity
                                    .getAccommodationTypeId()));

                    return AccommodationMapper.toDomain(entity, accommodationType);
                })
                .collect(Collectors.toList());
    }
}