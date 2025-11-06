package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.accommodationType.*;
import com.aogdev.rural.application.port.out.accommodationType.*;
import com.aogdev.rural.domain.exception.accommodationType.AccommodationTypeAlreadyExistsException;
import com.aogdev.rural.domain.exception.accommodationType.AccommodationTypeNotFoundException;
import com.aogdev.rural.domain.model.AccommodationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AccommodationTypeService implements
        CreateAccommodationTypeUseCase,
        GetAccommodationTypeUseCase,
        FindAccommodationTypeUseCase,
        UpdateAccommodationTypeUseCase,
        DeleteAccommodationTypeUseCase,
        ListAccommodationTypesUseCase {

    private final SaveAccommodationTypePort saveAccommodationTypePort;
    private final LoadAccommodationTypePort loadAccommodationTypePort;
    private final FindAccommodationTypeByNamePort findAccommodationTypeByNamePort;
    private final ListAccommodationTypesPort listAccommodationTypesPort;
    private final DeleteAccommodationTypePort deleteAccommodationTypePort;

    @Override
    public AccommodationType create(String name, String description) {
        log.info("Creating new accommodation type with name: {}", name);

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        findAccommodationTypeByNamePort.findByName(name)
                .ifPresent(existing -> {
                    throw new AccommodationTypeAlreadyExistsException(name);
                });

        AccommodationType accommodationType = new AccommodationType(
                null,
                name,
                description
        );

        AccommodationType savedType = saveAccommodationTypePort.save(accommodationType);
        log.info("Accommodation type created successfully with id: {}", savedType.id());

        return savedType;
    }

    @Override
    public AccommodationType getById(Short id) {
        log.debug("Fetching accommodation type with id: {}", id);

        return loadAccommodationTypePort.loadById(id)
                .orElseThrow(() -> new AccommodationTypeNotFoundException(id));
    }

    @Override
    public Optional<AccommodationType> findByName(String name) {
        log.debug("Finding accommodation type by name: {}", name);

        return findAccommodationTypeByNamePort.findByName(name);
    }

    @Override
    public AccommodationType update(Short id, String name, String description) {
        log.info("Updating accommodation type with id: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Accommodation type ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        AccommodationType existingType = loadAccommodationTypePort.loadById(id)
                .orElseThrow(() -> new AccommodationTypeNotFoundException(id));

        if (!existingType.name().equals(name)) {
            findAccommodationTypeByNamePort.findByName(name).ifPresent(type -> {
                if (!type.id().equals(id)) {
                    throw new AccommodationTypeAlreadyExistsException(name);
                }
            });
        }

        AccommodationType updatedType = new AccommodationType(
                id,
                name,
                description
        );

        AccommodationType savedType = saveAccommodationTypePort.save(updatedType);
        log.info("Accommodation type updated successfully with id: {}", savedType.id());

        return savedType;
    }

    @Override
    public void delete(Short id) {
        log.info("Deleting accommodation type with id: {}", id);

        loadAccommodationTypePort.loadById(id)
                .orElseThrow(() -> new AccommodationTypeNotFoundException(id));

        deleteAccommodationTypePort.deleteById(id);
        log.info("Accommodation type deleted successfully with id: {}", id);
    }

    @Override
    public List<AccommodationType> listAll() {
        log.debug("Listing all accommodation types");

        return listAccommodationTypesPort.findAll();
    }
}