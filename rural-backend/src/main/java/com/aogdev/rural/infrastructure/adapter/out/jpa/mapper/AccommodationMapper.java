package com.aogdev.rural.infrastructure.adapter.out.jpa.mapper;

import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.domain.valueobject.Money;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AccommodationJpaEntity;

import java.util.Currency;

public class AccommodationMapper {

    public static Accommodation toDomain(AccommodationJpaEntity entity, AccommodationType accommodationType) {
        if (entity == null) {
            return null;
        }

        return new Accommodation(
                entity.getId(),
                accommodationType,
                entity.getName(),
                new Money(
                        entity.getPricePerNight(),
                        Currency.getInstance(entity.getCurrency())
                ),
                entity.getBedCapacity(),
                entity.getActive()
        );
    }

    public static AccommodationJpaEntity toEntity(Accommodation accommodation) {
        if (accommodation == null) {
            return null;
        }

        return AccommodationJpaEntity.builder()
                .id(accommodation.id())
                .accommodationTypeId(accommodation.accommodationType().id())
                .name(accommodation.name())
                .pricePerNight(accommodation.pricePerNight().value())
                .currency(accommodation.pricePerNight().currency().getCurrencyCode())
                .bedCapacity(accommodation.bedCapacity())
                .active(accommodation.active())
                .build();
    }
}