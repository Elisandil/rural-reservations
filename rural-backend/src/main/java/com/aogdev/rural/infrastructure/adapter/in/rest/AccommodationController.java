package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.application.port.in.accommodation.*;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.AccommodationResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.CreateAccommodationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.UpdateAccommodationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.mapper.AccommodationRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final CreateAccommodationUseCase createAccommodationUseCase;
    private final GetAccommodationUseCase getAccommodationUseCase;
    private final FindAccommodationUseCase findAccommodationUseCase;
    private final UpdateAccommodationUseCase updateAccommodationUseCase;
    private final ActivateAccommodationUseCase activateAccommodationUseCase;
    private final DeactivateAccommodationUseCase deactivateAccommodationUseCase;
    private final ListAccommodationsUseCase listAccommodationsUseCase;

    @PostMapping
    public ResponseEntity<AccommodationResponse> createAccommodation(
            @Valid @RequestBody CreateAccommodationRequest request) {

        log.info("REST request to create accommodation with name: {}", request.name());

        CreateAccommodationCommand command = AccommodationRestMapper.toCommand(request);
        Accommodation accommodation = createAccommodationUseCase.create(command);
        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationResponse> getAccommodation(@PathVariable Long id) {
        log.info("REST request to get accommodation by id: {}", id);

        Accommodation accommodation = getAccommodationUseCase.getById(id);
        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<AccommodationResponse> getAccommodationByName(@PathVariable String name) {
        log.info("REST request to get accommodation by name: {}", name);

        Optional<Accommodation> accommodationOpt = findAccommodationUseCase.findByName(name);

        return accommodationOpt
                .map(AccommodationRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AccommodationResponse>> listAccommodations(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly,
            @RequestParam(required = false) Short accommodationTypeId) {

        log.info("REST request to list accommodations (activeOnly: {}, typeId: {})",
                activeOnly, accommodationTypeId);

        List<Accommodation> accommodations;

        if (accommodationTypeId != null) {
            accommodations = listAccommodationsUseCase.listByAccommodationType(accommodationTypeId);
        } else if (activeOnly) {
            accommodations = listAccommodationsUseCase.listActive();
        } else {
            accommodations = listAccommodationsUseCase.listAll();
        }

        List<AccommodationResponse> response = AccommodationRestMapper.toResponseList(accommodations);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccommodationResponse> updateAccommodation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccommodationRequest request) {

        log.info("REST request to update accommodation with id: {}", id);

        UpdateAccommodationCommand command = AccommodationRestMapper.toCommand(id, request);
        Accommodation accommodation = updateAccommodationUseCase.update(command);
        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateAccommodation(@PathVariable Long id) {
        log.info("REST request to activate accommodation with id: {}", id);

        activateAccommodationUseCase.activate(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateAccommodation(@PathVariable Long id) {
        log.info("REST request to deactivate accommodation with id: {}", id);

        deactivateAccommodationUseCase.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}