package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.AccommodationTypeResponse;

import java.util.List;
import java.util.stream.Collectors;

public class AccommodationTypeRestMapper {

    public static AccommodationTypeResponse toResponse(AccommodationType accommodationType) {
        return new AccommodationTypeResponse(
                accommodationType.id(),
                accommodationType.name(),
                accommodationType.description()
        );
    }

    public static List<AccommodationTypeResponse> toResponseList(List<AccommodationType> accommodationTypes) {
        return accommodationTypes.stream()
                .map(AccommodationTypeRestMapper::toResponse)
                .collect(Collectors.toList());
    }
}