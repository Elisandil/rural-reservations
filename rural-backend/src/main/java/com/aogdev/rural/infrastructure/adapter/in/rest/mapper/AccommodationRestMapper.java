package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.application.port.in.accommodation.CreateAccommodationCommand;
import com.aogdev.rural.application.port.in.accommodation.UpdateAccommodationCommand;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.valueobject.Money;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.AccommodationResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.CreateAccommodationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.UpdateAccommodationRequest;

import java.util.List;
import java.util.stream.Collectors;

public class AccommodationRestMapper {

    public static CreateAccommodationCommand toCommand(CreateAccommodationRequest request) {
        return new CreateAccommodationCommand(
                request.accommodationTypeId(),
                request.name(),
                Money.euros(request.pricePerNight()),
                request.bedCapacity()
        );
    }

    public static UpdateAccommodationCommand toCommand(Long id, UpdateAccommodationRequest request) {
        return new UpdateAccommodationCommand(
                id,
                request.accommodationTypeId(),
                request.name(),
                Money.euros(request.pricePerNight()),
                request.bedCapacity()
        );
    }

    public static AccommodationResponse toResponse(Accommodation accommodation) {
        return new AccommodationResponse(
                accommodation.id(),
                accommodation.accommodationType().id(),
                accommodation.accommodationType().name(),
                accommodation.name(),
                accommodation.pricePerNight().value(),
                accommodation.pricePerNight().currency().getCurrencyCode(),
                accommodation.bedCapacity(),
                accommodation.active()
        );
    }

    public static List<AccommodationResponse> toResponseList(List<Accommodation> accommodations) {
        return accommodations.stream()
                .map(AccommodationRestMapper::toResponse)
                .collect(Collectors.toList());
    }
}