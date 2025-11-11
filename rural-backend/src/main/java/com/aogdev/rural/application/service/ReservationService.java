package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.reservation.*;
import com.aogdev.rural.application.port.out.accommodation.LoadAccommodationPort;
import com.aogdev.rural.application.port.out.admin.LoadAdminPort;
import com.aogdev.rural.application.port.out.reservation.*;
import com.aogdev.rural.domain.exception.accommodation.AccommodationNotFoundException;
import com.aogdev.rural.domain.exception.admin.AdminNotFoundException;
import com.aogdev.rural.domain.exception.reservation.InsufficientCapacityException;
import com.aogdev.rural.domain.exception.reservation.ReservationNotFoundException;
import com.aogdev.rural.domain.exception.reservation.ReservationOverlapException;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ReservationService implements
        CreateReservationUseCase,
        GetReservationUseCase,
        UpdateReservationUseCase,
        MarkReservationAsPaidUseCase,
        CancelReservationUseCase,
        ListReservationsUseCase {

    private final SaveReservationPort saveReservationPort;
    private final LoadReservationPort loadReservationPort;
    private final DeleteReservationPort deleteReservationPort;
    private final ListReservationsPort listReservationsPort;
    private final FindOverlappingReservationsPort findOverlappingReservationsPort;
    private final LoadAccommodationPort loadAccommodationPort;
    private final LoadAdminPort loadAdminPort;

    @Override
    public Reservation create(CreateReservationCommand command) {
        log.info("Creating new reservation for accommodation: {}", command.accommodationId());

        Accommodation accommodation = loadAccommodationPort.loadById(command.accommodationId())
                .orElseThrow(() -> new AccommodationNotFoundException(command.accommodationId()));

        loadAdminPort.loadById(command.adminId())
                .orElseThrow(() -> new AdminNotFoundException(command.adminId()));

        if (!accommodation.hasCapacityFor(command.bedsReserved())) {
            throw new InsufficientCapacityException(command.bedsReserved(), accommodation.bedCapacity());
        }

        List<Reservation> overlapping = findOverlappingReservationsPort.findOverlapping(
                command.accommodationId(),
                command.dateRange(),
                null
        );

        if (!overlapping.isEmpty()) {
            throw new ReservationOverlapException(command.accommodationId());
        }

        Money totalPrice = accommodation.calculateTotalPrice((int) command.dateRange().nights());

        Reservation reservation = new Reservation(
                null,
                command.accommodationId(),
                command.adminId(),
                command.dateRange(),
                command.bedsReserved(),
                totalPrice,
                false,
                LocalDate.now(),
                command.notes()
        );

        Reservation savedReservation = saveReservationPort.save(reservation);
        log.info("Reservation created successfully with id: {}", savedReservation.id());

        return savedReservation;
    }

    @Override
    public Reservation getById(Long id) {
        log.debug("Fetching reservation with id: {}", id);

        return loadReservationPort.loadById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    @Override
    public Reservation update(UpdateReservationCommand command) {
        log.info("Updating reservation with id: {}", command.id());

        Reservation existingReservation = loadReservationPort.loadById(command.id())
                .orElseThrow(() -> new ReservationNotFoundException(command.id()));

        Accommodation accommodation = loadAccommodationPort.loadById(command.accommodationId())
                .orElseThrow(() -> new AccommodationNotFoundException(command.accommodationId()));

        loadAdminPort.loadById(command.adminId())
                .orElseThrow(() -> new AdminNotFoundException(command.adminId()));

        if (!accommodation.hasCapacityFor(command.bedsReserved())) {
            throw new InsufficientCapacityException(command.bedsReserved(), accommodation.bedCapacity());
        }

        List<Reservation> overlapping = findOverlappingReservationsPort.findOverlapping(
                command.accommodationId(),
                command.dateRange(),
                command.id()
        );

        if (!overlapping.isEmpty()) {
            throw new ReservationOverlapException(command.accommodationId());
        }

        Money totalPrice = accommodation.calculateTotalPrice((int) command.dateRange().nights());

        Reservation updatedReservation = new Reservation(
                command.id(),
                command.accommodationId(),
                command.adminId(),
                command.dateRange(),
                command.bedsReserved(),
                totalPrice,
                existingReservation.paid(),
                existingReservation.bookingDate(),
                command.notes()
        );

        Reservation savedReservation = saveReservationPort.save(updatedReservation);
        log.info("Reservation updated successfully with id: {}", savedReservation.id());

        return savedReservation;
    }

    @Override
    public void markAsPaid(Long id) {
        log.info("Marking reservation as paid with id: {}", id);

        Reservation reservation = loadReservationPort.loadById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (!reservation.isPaid()) {
            Reservation paidReservation = reservation.markAsPaid();
            saveReservationPort.save(paidReservation);

            log.info("Reservation marked as paid successfully with id: {}", id);
        } else {
            log.debug("Reservation already paid with id: {}", id);
        }
    }

    @Override
    public void cancel(Long id) {
        log.info("Cancelling reservation with id: {}", id);

        loadReservationPort.loadById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        deleteReservationPort.deleteById(id);
        log.info("Reservation cancelled successfully with id: {}", id);
    }

    @Override
    public List<Reservation> listAll() {
        log.debug("Listing all reservations");

        return listReservationsPort.findAll();
    }

    @Override
    public List<Reservation> listByAccommodation(Long accommodationId) {
        log.debug("Listing reservations by accommodation: {}", accommodationId);

        loadAccommodationPort.loadById(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException(accommodationId));

        return listReservationsPort.findByAccommodationId(accommodationId);
    }

    @Override
    public List<Reservation> listByAdmin(Long adminId) {
        log.debug("Listing reservations by admin: {}", adminId);

        loadAdminPort.loadById(adminId)
                .orElseThrow(() -> new AdminNotFoundException(adminId));

        return listReservationsPort.findByAdminId(adminId);
    }

    @Override
    public List<Reservation> listByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Listing reservations by date range: {} to {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        return listReservationsPort.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Reservation> listUnpaid() {
        log.debug("Listing unpaid reservations");

        return listReservationsPort.findUnpaid();
    }
}