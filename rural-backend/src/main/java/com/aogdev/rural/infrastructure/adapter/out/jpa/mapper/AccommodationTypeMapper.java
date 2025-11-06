package com.aogdev.rural.infrastructure.adapter.out.jpa.mapper;

import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AccommodationTypeJpaEntity;

public class AccommodationTypeMapper {

    public static AccommodationType toDomain(AccommodationTypeJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AccommodationType(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public static AccommodationTypeJpaEntity toEntity(AccommodationType accommodationType) {
        if (accommodationType == null) {
            return null;
        }

        return AccommodationTypeJpaEntity.builder()
                .id(accommodationType.id())
                .name(accommodationType.name())
                .description(accommodationType.description())
                .build();
    }
}