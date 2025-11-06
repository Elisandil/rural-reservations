package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.application.port.out.accommodationType.*;
import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AccommodationTypeJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.mapper.AccommodationTypeMapper;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationTypeJpaRepository;
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
public class AccommodationTypePersistenceAdapter implements
        SaveAccommodationTypePort,
        LoadAccommodationTypePort,
        FindAccommodationTypeByNamePort,
        ListAccommodationTypesPort,
        DeleteAccommodationTypePort {

    private final AccommodationTypeJpaRepository repository;

    @Override
    @Transactional
    public AccommodationType save(AccommodationType accommodationType) {
        log.debug("Saving accommodation type to database: {}", accommodationType.name());

        try {
            AccommodationTypeJpaEntity entity = AccommodationTypeMapper.toEntity(accommodationType);
            AccommodationTypeJpaEntity savedEntity = repository.save(entity);
            log.debug("Accommodation type saved successfully with id: {}", savedEntity.getId());

            return AccommodationTypeMapper.toDomain(savedEntity);
        } catch (Exception ex) {
            log.error("Error saving accommodation type: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to save accommodation type", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccommodationType> loadById(Short id) {
        log.debug("Loading accommodation type by id: {}", id);

        return repository.findById(id)
                .map(AccommodationTypeMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccommodationType> findByName(String name) {
        log.debug("Finding accommodation type by name: {}", name);

        return repository.findByName(name)
                .map(AccommodationTypeMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationType> findAll() {
        log.debug("Finding all accommodation types");

        return repository.findAll()
                .stream()
                .map(AccommodationTypeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Short id) {
        log.debug("Deleting accommodation type by id: {}", id);

        try {
            repository.deleteById(id);
            log.debug("Accommodation type deleted successfully with id: {}", id);
        } catch (Exception ex) {
            log.error("Error deleting accommodation type: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to delete accommodation type", ex);
        }
    }
}