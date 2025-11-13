package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.application.port.in.reservation.CreateReservationCommand;
import com.aogdev.rural.application.port.in.reservation.UpdateReservationCommand;
import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.DateRange;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.CreateReservationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.ReservationResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.UpdateReservationRequest;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReservationRestMapper {

    public static CreateReservationCommand toCommand(CreateReservationRequest request) {
        return new CreateReservationCommand(
                request.accommodationId(),
                request.adminId(),
                new DateRange(request.startDate(), request.endDate()),
                request.bedsReserved(),
                request.notes()
        );
    }

    public static UpdateReservationCommand toCommand(Long id, UpdateReservationRequest request) {
        return new UpdateReservationCommand(
                id,
                request.accommodationId(),
                request.adminId(),
                new DateRange(request.startDate(), request.endDate()),
                request.bedsReserved(),
                request.notes()
        );
    }

    public static ReservationResponse toResponse(Reservation reservation,
                                                 String accommodationName,
                                                 String adminFullName) {
        return new ReservationResponse(
                reservation.id(),
                reservation.accommodationId(),
                accommodationName,
                reservation.adminId(),
                adminFullName,
                reservation.dateRange().startDate(),
                reservation.dateRange().endDate(),
                reservation.nights(),
                reservation.bedsReserved(),
                reservation.totalPrice().value(),
                reservation.totalPrice().currency().getCurrencyCode(),
                reservation.paid(),
                reservation.bookingDate(),
                reservation.notes()
        );
    }

    public static List<ReservationResponse> toResponseList(List<Reservation> reservations,
                Function<Long, String> accommodationNameResolver,
                Function<Long, String> adminNameResolver) {

        return reservations.stream()
                .map(r -> toResponse(
                        r,
                        accommodationNameResolver.apply(r.accommodationId()),
                        adminNameResolver.apply(r.adminId())
                ))
                .collect(Collectors.toList());
    }
}