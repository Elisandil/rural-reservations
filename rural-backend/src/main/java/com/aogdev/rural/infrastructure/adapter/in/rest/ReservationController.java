package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.application.port.in.reservation.*;
import com.aogdev.rural.application.port.out.accommodation.LoadAccommodationPort;
import com.aogdev.rural.application.port.out.admin.LoadAdminPort;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.CreateReservationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.ReservationResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.UpdateReservationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.mapper.ReservationRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final UpdateReservationUseCase updateReservationUseCase;
    private final MarkReservationAsPaidUseCase markReservationAsPaidUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final ListReservationsUseCase listReservationsUseCase;
    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadAdminPort loadAdminPort;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {

        log.info("REST request to create reservation for accommodation: {}", request.accommodationId());

        CreateReservationCommand command = ReservationRestMapper.toCommand(request);
        Reservation reservation = createReservationUseCase.create(command);
        ReservationResponse response = buildResponse(reservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        log.info("REST request to get reservation by id: {}", id);

        Reservation reservation = getReservationUseCase.getById(id);
        ReservationResponse response = buildResponse(reservation);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> listReservations(
            @RequestParam(required = false) Long accommodationId,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "false") boolean unpaidOnly) {

        log.info("REST request to list reservations");

        List<Reservation> reservations;

        if (accommodationId != null) {
            reservations = listReservationsUseCase.listByAccommodation(accommodationId);
        } else if (adminId != null) {
            reservations = listReservationsUseCase.listByAdmin(adminId);
        } else if (startDate != null && endDate != null) {
            reservations = listReservationsUseCase.listByDateRange(startDate, endDate);
        } else if (unpaidOnly) {
            reservations = listReservationsUseCase.listUnpaid();
        } else {
            reservations = listReservationsUseCase.listAll();
        }

        List<ReservationResponse> response = ReservationRestMapper.toResponseList(
                reservations,
                this::resolveAccommodationName,
                this::resolveAdminName
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReservationRequest request) {

        log.info("REST request to update reservation with id: {}", id);

        UpdateReservationCommand command = ReservationRestMapper.toCommand(id, request);
        Reservation reservation = updateReservationUseCase.update(command);
        ReservationResponse response = buildResponse(reservation);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<Void> markReservationAsPaid(@PathVariable Long id) {
        log.info("REST request to mark reservation as paid with id: {}", id);

        markReservationAsPaidUseCase.markAsPaid(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        log.info("REST request to cancel reservation with id: {}", id);

        cancelReservationUseCase.cancel(id);

        return ResponseEntity.noContent().build();
    }

    private ReservationResponse buildResponse(Reservation reservation) {
        String accommodationName = resolveAccommodationName(reservation.accommodationId());
        String adminName = resolveAdminName(reservation.adminId());

        return ReservationRestMapper.toResponse(reservation, accommodationName, adminName);
    }

    private String resolveAccommodationName(Long accommodationId) {
        return loadAccommodationPort.loadById(accommodationId)
                .map(Accommodation::name)
                .orElse("Unknown");
    }

    private String resolveAdminName(Long adminId) {
        return loadAdminPort.loadById(adminId)
                .map(a -> a.name().fullName())
                .orElse("Unknown");
    }
}