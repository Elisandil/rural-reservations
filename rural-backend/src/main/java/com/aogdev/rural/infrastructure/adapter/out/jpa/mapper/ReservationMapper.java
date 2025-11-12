package com.aogdev.rural.infrastructure.adapter.out.jpa.mapper;

import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.DateRange;
import com.aogdev.rural.domain.valueobject.Money;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.ReservationJpaEntity;

import java.util.Currency;

public class ReservationMapper {

    public static Reservation toDomain(ReservationJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Reservation(
                entity.getId(),
                entity.getAccommodationId(),
                entity.getAdminId(),
                new DateRange(entity.getStartDate(), entity.getEndDate()),
                entity.getBedsReserved(),
                new Money(
                        entity.getTotalPrice(),
                        Currency.getInstance(entity.getCurrency())
                ),
                entity.getPaid(),
                entity.getBookingDate(),
                entity.getNotes()
        );
    }

    public static ReservationJpaEntity toEntity(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        return ReservationJpaEntity.builder()
                .id(reservation.id())
                .accommodationId(reservation.accommodationId())
                .adminId(reservation.adminId())
                .startDate(reservation.dateRange().startDate())
                .endDate(reservation.dateRange().endDate())
                .bedsReserved(reservation.bedsReserved())
                .totalPrice(reservation.totalPrice().value())
                .currency(reservation.totalPrice().currency().getCurrencyCode())
                .paid(reservation.paid())
                .bookingDate(reservation.bookingDate())
                .notes(reservation.notes())
                .build();
    }
}