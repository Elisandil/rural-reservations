package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.application.port.in.accommodationType.*;
import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.AccommodationTypeResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.CreateAccommodationTypeRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.UpdateAccommodationTypeRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.mapper.AccommodationTypeRestMapper;
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
@RequestMapping("/api/v1/accommodation-types")
@RequiredArgsConstructor
public class AccommodationTypeController {

    private final CreateAccommodationTypeUseCase createAccommodationTypeUseCase;
    private final GetAccommodationTypeUseCase getAccommodationTypeUseCase;
    private final FindAccommodationTypeUseCase findAccommodationTypeUseCase;
    private final UpdateAccommodationTypeUseCase updateAccommodationTypeUseCase;
    private final DeleteAccommodationTypeUseCase deleteAccommodationTypeUseCase;
    private final ListAccommodationTypesUseCase listAccommodationTypesUseCase;

    @PostMapping
    public ResponseEntity<AccommodationTypeResponse> createAccommodationType(
            @Valid @RequestBody CreateAccommodationTypeRequest request) {

        log.info("REST request to create accommodation type with name: {}", request.name());

        AccommodationType accommodationType = createAccommodationTypeUseCase.create(
                request.name(),
                request.description()
        );
        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationTypeResponse> getAccommodationType(@PathVariable Short id) {
        log.info("REST request to get accommodation type by id: {}", id);

        AccommodationType accommodationType = getAccommodationTypeUseCase.getById(id);
        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<AccommodationTypeResponse> getAccommodationTypeByName(@PathVariable String name) {
        log.info("REST request to get accommodation type by name: {}", name);

        Optional<AccommodationType> typeOpt = findAccommodationTypeUseCase.findByName(name);

        return typeOpt
                .map(AccommodationTypeRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AccommodationTypeResponse>> listAccommodationTypes() {
        log.info("REST request to list all accommodation types");

        List<AccommodationType> accommodationTypes = listAccommodationTypesUseCase.listAll();
        List<AccommodationTypeResponse> response = AccommodationTypeRestMapper.toResponseList(accommodationTypes);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccommodationTypeResponse> updateAccommodationType(
            @PathVariable Short id,
            @Valid @RequestBody UpdateAccommodationTypeRequest request) {

        log.info("REST request to update accommodation type with id: {}", id);

        AccommodationType accommodationType = updateAccommodationTypeUseCase.update(
                id,
                request.name(),
                request.description()
        );
        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodationType(@PathVariable Short id) {
        log.info("REST request to delete accommodation type with id: {}", id);

        deleteAccommodationTypeUseCase.delete(id);

        return ResponseEntity.noContent().build();
    }
}