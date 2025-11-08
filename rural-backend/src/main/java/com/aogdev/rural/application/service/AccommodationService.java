package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.accommodation.*;
import com.aogdev.rural.application.port.out.accommodation.*;
import com.aogdev.rural.application.port.out.accommodationType.LoadAccommodationTypePort;
import com.aogdev.rural.domain.exception.accommodation.AccommodationAlreadyExistsException;
import com.aogdev.rural.domain.exception.accommodation.AccommodationNotActiveException;
import com.aogdev.rural.domain.exception.accommodation.AccommodationNotFoundException;
import com.aogdev.rural.domain.exception.accommodationType.AccommodationTypeNotFoundException;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.AccommodationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AccommodationService implements
        CreateAccommodationUseCase,
        GetAccommodationUseCase,
        FindAccommodationUseCase,
        UpdateAccommodationUseCase,
        ActivateAccommodationUseCase,
        DeactivateAccommodationUseCase,
        ListAccommodationsUseCase {

    private final SaveAccommodationPort saveAccommodationPort;
    private final LoadAccommodationPort loadAccommodationPort;
    private final FindAccommodationByNamePort findAccommodationByNamePort;
    private final ListAccommodationsPort listAccommodationsPort;
    private final LoadAccommodationTypePort loadAccommodationTypePort;

    @Override
    public Accommodation create(CreateAccommodationCommand command) {
        log.info("Creating new accommodation with name: {}", command.name());

        findAccommodationByNamePort.findByName(command.name())
                .ifPresent(existing -> {
                    throw new AccommodationAlreadyExistsException(command.name());
                });

        AccommodationType accommodationType = loadAccommodationTypePort
                .loadById(command.accommodationTypeId())
                .orElseThrow(() -> new AccommodationTypeNotFoundException(command.accommodationTypeId()));

        Accommodation accommodation = new Accommodation(
                null,
                accommodationType,
                command.name(),
                command.pricePerNight(),
                command.bedCapacity(),
                true
        );

        Accommodation savedAccommodation = saveAccommodationPort.save(accommodation);
        log.info("Accommodation created successfully with id: {}", savedAccommodation.id());

        return savedAccommodation;
    }

    @Override
    public Accommodation getById(Long id) {
        log.debug("Fetching accommodation with id: {}", id);

        return loadAccommodationPort.loadById(id)
                .orElseThrow(() -> new AccommodationNotFoundException(id));
    }

    @Override
    public Optional<Accommodation> findByName(String name) {
        log.debug("Finding accommodation by name: {}", name);

        return findAccommodationByNamePort.findByName(name);
    }

    @Override
    public Accommodation update(UpdateAccommodationCommand command) {
        log.info("Updating accommodation with id: {}", command.id());

        Accommodation existingAccommodation = loadAccommodationPort.loadById(command.id())
                .orElseThrow(() -> new AccommodationNotFoundException(command.id()));

        if (!existingAccommodation.isActive()) {
            log.warn("Attempted to update inactive accommodation with id: {}", command.id());
            throw new AccommodationNotActiveException(command.id());
        }

        if (!existingAccommodation.name().equals(command.name())) {
            findAccommodationByNamePort.findByName(command.name()).ifPresent(accommodation -> {
                if (!accommodation.id().equals(command.id())) {
                    throw new AccommodationAlreadyExistsException(command.name());
                }
            });
        }

        AccommodationType accommodationType = loadAccommodationTypePort
                .loadById(command.accommodationTypeId())
                .orElseThrow(() -> new AccommodationTypeNotFoundException(command.accommodationTypeId()));

        Accommodation updatedAccommodation = new Accommodation(
                command.id(),
                accommodationType,
                command.name(),
                command.pricePerNight(),
                command.bedCapacity(),
                existingAccommodation.active()
        );

        Accommodation savedAccommodation = saveAccommodationPort.save(updatedAccommodation);
        log.info("Accommodation updated successfully with id: {}", savedAccommodation.id());

        return savedAccommodation;
    }

    @Override
    public void activate(Long id) {
        log.info("Activating accommodation with id: {}", id);

        Accommodation accommodation = loadAccommodationPort.loadById(id)
                .orElseThrow(() -> new AccommodationNotFoundException(id));

        if (!accommodation.isActive()) {
            Accommodation activatedAccommodation = new Accommodation(
                    accommodation.id(),
                    accommodation.accommodationType(),
                    accommodation.name(),
                    accommodation.pricePerNight(),
                    accommodation.bedCapacity(),
                    true
            );
            saveAccommodationPort.save(activatedAccommodation);

            log.info("Accommodation activated successfully with id: {}", id);
        } else {
            log.debug("Accommodation already active with id: {}", id);
        }
    }

    @Override
    public void deactivate(Long id) {
        log.info("Deactivating accommodation with id: {}", id);

        Accommodation accommodation = loadAccommodationPort.loadById(id)
                .orElseThrow(() -> new AccommodationNotFoundException(id));

        if (accommodation.isActive()) {
            Accommodation deactivatedAccommodation = new Accommodation(
                    accommodation.id(),
                    accommodation.accommodationType(),
                    accommodation.name(),
                    accommodation.pricePerNight(),
                    accommodation.bedCapacity(),
                    false
            );
            saveAccommodationPort.save(deactivatedAccommodation);

            log.info("Accommodation deactivated successfully with id: {}", id);
        } else {
            log.debug("Accommodation already inactive with id: {}", id);
        }
    }

    @Override
    public List<Accommodation> listAll() {
        log.debug("Listing all accommodations");

        return listAccommodationsPort.findAll();
    }

    @Override
    public List<Accommodation> listActive() {
        log.debug("Listing active accommodations");

        return listAccommodationsPort.findAllActive();
    }

    @Override
    public List<Accommodation> listByAccommodationType(Short accommodationTypeId) {
        log.debug("Listing accommodations by type: {}", accommodationTypeId);

        loadAccommodationTypePort.loadById(accommodationTypeId)
                .orElseThrow(() -> new AccommodationTypeNotFoundException(accommodationTypeId));

        return listAccommodationsPort.findByAccommodationTypeId(accommodationTypeId);
    }
}