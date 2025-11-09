package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.application.port.in.accommodation.CreateAccommodationCommand;
import com.aogdev.rural.application.port.in.accommodation.UpdateAccommodationCommand;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.domain.valueobject.Money;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.AccommodationResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.CreateAccommodationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.UpdateAccommodationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AccommodationRestMapper Tests")
class AccommodationRestMapperTest {

    @Test
    @DisplayName("Should map CreateAccommodationRequest to CreateAccommodationCommand")
    void shouldMapCreateRequestToCommand() {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                (short) 1,
                "Casa del Bosque",
                new BigDecimal("85.00"),
                4
        );

        CreateAccommodationCommand command = AccommodationRestMapper.toCommand(request);

        assertThat(command.accommodationTypeId()).isEqualTo((short) 1);
        assertThat(command.name()).isEqualTo("Casa del Bosque");
        assertThat(command.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("85.00"));
        assertThat(command.pricePerNight().currency().getCurrencyCode()).isEqualTo("EUR");
        assertThat(command.bedCapacity()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should map UpdateAccommodationRequest to UpdateAccommodationCommand")
    void shouldMapUpdateRequestToCommand() {
        UpdateAccommodationRequest request = new UpdateAccommodationRequest(
                (short) 1,
                "Casa del Bosque",
                new BigDecimal("95.00"),
                6
        );

        UpdateAccommodationCommand command = AccommodationRestMapper.toCommand(1L, request);

        assertThat(command.id()).isEqualTo(1L);
        assertThat(command.accommodationTypeId()).isEqualTo((short) 1);
        assertThat(command.name()).isEqualTo("Casa del Bosque");
        assertThat(command.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("95.00"));
        assertThat(command.pricePerNight().currency().getCurrencyCode()).isEqualTo("EUR");
        assertThat(command.bedCapacity()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should map Accommodation to AccommodationResponse")
    void shouldMapAccommodationToResponse() {
        AccommodationType type = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo"
        );

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.accommodationTypeId()).isEqualTo((short) 1);
        assertThat(response.accommodationTypeName()).isEqualTo("Casa Rural");
        assertThat(response.name()).isEqualTo("Casa del Bosque");
        assertThat(response.pricePerNight()).isEqualByComparingTo(new BigDecimal("85.00"));
        assertThat(response.currency()).isEqualTo("EUR");
        assertThat(response.bedCapacity()).isEqualTo(4);
        assertThat(response.active()).isTrue();
    }

    @Test
    @DisplayName("Should map Accommodation with inactive status to response")
    void shouldMapInactiveAccommodationToResponse() {
        AccommodationType type = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo"
        );

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                false
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response.active()).isFalse();
    }

    @Test
    @DisplayName("Should map Accommodation with null id to response")
    void shouldMapAccommodationWithNullIdToResponse() {
        AccommodationType type = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo"
        );

        Accommodation accommodation = new Accommodation(
                null,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response.id()).isNull();
    }

    @Test
    @DisplayName("Should map list of Accommodations to list of AccommodationResponses")
    void shouldMapAccommodationListToResponseList() {
        AccommodationType type = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo"
        );

        Accommodation accommodation1 = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        Accommodation accommodation2 = new Accommodation(
                2L,
                type,
                "Villa del Mar",
                Money.euros(120.00),
                6,
                true
        );

        Accommodation accommodation3 = new Accommodation(
                3L,
                type,
                "Apartamento Centro",
                Money.euros(60.00),
                2,
                false
        );

        List<AccommodationResponse> responses = AccommodationRestMapper
                .toResponseList(List.of(accommodation1, accommodation2, accommodation3));

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).name()).isEqualTo("Casa del Bosque");
        assertThat(responses.get(1).id()).isEqualTo(2L);
        assertThat(responses.get(1).name()).isEqualTo("Villa del Mar");
        assertThat(responses.get(2).id()).isEqualTo(3L);
        assertThat(responses.get(2).name()).isEqualTo("Apartamento Centro");
    }

    @Test
    @DisplayName("Should map empty list to empty response list")
    void shouldMapEmptyListToEmptyResponseList() {
        List<AccommodationResponse> responses = AccommodationRestMapper.toResponseList(List.of());

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Should preserve order when mapping list")
    void shouldPreserveOrderWhenMappingList() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Descripción");

        Accommodation accommodation1 = new Accommodation(3L, type, "C", Money.euros(100.00), 6, true);
        Accommodation accommodation2 = new Accommodation(1L, type, "A", Money.euros(50.00), 2, true);
        Accommodation accommodation3 = new Accommodation(2L, type, "B", Money.euros(75.00), 4, true);

        List<AccommodationResponse> responses = AccommodationRestMapper
                .toResponseList(List.of(accommodation1, accommodation2, accommodation3));

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).id()).isEqualTo(3L);
        assertThat(responses.get(1).id()).isEqualTo(1L);
        assertThat(responses.get(2).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void shouldHandleSpecialCharactersInName() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Descripción");

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa «El Olivo»",
                Money.euros(85.00),
                4,
                true
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response.name()).isEqualTo("Casa «El Olivo»");
    }

    @Test
    @DisplayName("Should handle very high prices")
    void shouldHandleVeryHighPrices() {
        AccommodationType type = new AccommodationType((short) 1, "Villa de Lujo", "Premium");

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Villa Presidencial",
                Money.euros(new BigDecimal("9999.99")),
                10,
                true
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response.pricePerNight()).isEqualByComparingTo(new BigDecimal("9999.99"));
    }

    @Test
    @DisplayName("Should handle minimum price")
    void shouldHandleMinimumPrice() {
        AccommodationType type = new AccommodationType((short) 1, "Hostal", "Económico");

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Habitación Básica",
                Money.euros(new BigDecimal("0.01")),
                1,
                true
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response.pricePerNight()).isEqualByComparingTo(new BigDecimal("0.01"));
    }

    @Test
    @DisplayName("Should handle high bed capacity")
    void shouldHandleHighBedCapacity() {
        AccommodationType type = new AccommodationType((short) 1, "Albergue", "Grupo grande");

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Albergue Rural",
                Money.euros(200.00),
                20,
                true
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response.bedCapacity()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should handle minimum bed capacity")
    void shouldHandleMinimumBedCapacity() {
        AccommodationType type = new AccommodationType((short) 1, "Estudio", "Individual");

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Estudio Individual",
                Money.euros(40.00),
                1,
                true
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response.bedCapacity()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should map list with mixed active statuses")
    void shouldMapListWithMixedActiveStatuses() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Descripción");

        Accommodation accommodation1 = new Accommodation(1L, type, "Casa A", Money.euros(85.00), 4, true);
        Accommodation accommodation2 = new Accommodation(2L, type, "Casa B", Money.euros(90.00), 5, false);
        Accommodation accommodation3 = new Accommodation(3L, type, "Casa C", Money.euros(95.00), 6, true);

        List<AccommodationResponse> responses = AccommodationRestMapper
                .toResponseList(List.of(accommodation1, accommodation2, accommodation3));

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).active()).isTrue();
        assertThat(responses.get(1).active()).isFalse();
        assertThat(responses.get(2).active()).isTrue();
    }

    @Test
    @DisplayName("Should handle single element list")
    void shouldHandleSingleElementList() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Única casa");

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        List<AccommodationResponse> responses = AccommodationRestMapper.toResponseList(List.of(accommodation));

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(1L);
        assertThat(responses.getFirst().name()).isEqualTo("Casa del Bosque");
    }

    @Test
    @DisplayName("Should map all fields correctly in response")
    void shouldMapAllFieldsCorrectlyInResponse() {
        AccommodationType type = new AccommodationType(
                (short) 42,
                "Villa de Lujo",
                "Espectacular"
        );

        Accommodation accommodation = new Accommodation(
                123L,
                type,
                "Villa Paradise",
                Money.euros(new BigDecimal("250.50")),
                8,
                true
        );

        AccommodationResponse response = AccommodationRestMapper.toResponse(accommodation);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(123L);
        assertThat(response.accommodationTypeId()).isEqualTo((short) 42);
        assertThat(response.accommodationTypeName()).isEqualTo("Villa de Lujo");
        assertThat(response.name()).isEqualTo("Villa Paradise");
        assertThat(response.pricePerNight()).isEqualByComparingTo(new BigDecimal("250.50"));
        assertThat(response.currency()).isEqualTo("EUR");
        assertThat(response.bedCapacity()).isEqualTo(8);
        assertThat(response.active()).isTrue();
    }

    @Test
    @DisplayName("Should create Money as EUR currency when mapping from request")
    void shouldCreateMoneyAsEurWhenMappingFromRequest() {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                (short) 1,
                "Casa del Bosque",
                new BigDecimal("85.00"),
                4
        );

        CreateAccommodationCommand command = AccommodationRestMapper.toCommand(request);

        assertThat(command.pricePerNight().currency().getCurrencyCode()).isEqualTo("EUR");
        assertThat(command.pricePerNight().currency().getSymbol()).isEqualTo("€");
    }

    @Test
    @DisplayName("Should preserve precision when mapping prices")
    void shouldPreservePrecisionWhenMappingPrices() {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                (short) 1,
                "Casa del Bosque",
                new BigDecimal("85.99"),
                4
        );

        CreateAccommodationCommand command = AccommodationRestMapper.toCommand(request);

        assertThat(command.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("85.99"));
        assertThat(command.pricePerNight().value().scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should map different accommodation types correctly")
    void shouldMapDifferentAccommodationTypesCorrectly() {
        AccommodationType type1 = new AccommodationType((short) 1, "Casa Rural", "Desc1");
        AccommodationType type2 = new AccommodationType((short) 2, "Apartamento", "Desc2");

        Accommodation accommodation1 = new Accommodation(1L, type1, "Casa A", Money.euros(85.00), 4, true);
        Accommodation accommodation2 = new Accommodation(2L, type2, "Apto B", Money.euros(60.00), 2, true);

        List<AccommodationResponse> responses = AccommodationRestMapper
                .toResponseList(List.of(accommodation1, accommodation2));

        assertThat(responses.get(0).accommodationTypeId()).isEqualTo((short) 1);
        assertThat(responses.get(0).accommodationTypeName()).isEqualTo("Casa Rural");
        assertThat(responses.get(1).accommodationTypeId()).isEqualTo((short) 2);
        assertThat(responses.get(1).accommodationTypeName()).isEqualTo("Apartamento");
    }

    @Test
    @DisplayName("Should handle update command with different id")
    void shouldHandleUpdateCommandWithDifferentId() {
        UpdateAccommodationRequest request = new UpdateAccommodationRequest(
                (short) 1,
                "Casa Actualizada",
                new BigDecimal("100.00"),
                5
        );

        UpdateAccommodationCommand command = AccommodationRestMapper.toCommand(999L, request);

        assertThat(command.id()).isEqualTo(999L);
        assertThat(command.name()).isEqualTo("Casa Actualizada");
    }

    @Test
    @DisplayName("Should map accommodations with same type to responses")
    void shouldMapAccommodationsWithSameTypeToResponses() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Tradicional");

        Accommodation accommodation1 = new Accommodation(1L, type, "Casa 1", Money.euros(80.00), 4, true);
        Accommodation accommodation2 = new Accommodation(2L, type, "Casa 2", Money.euros(85.00), 4, true);
        Accommodation accommodation3 = new Accommodation(3L, type, "Casa 3", Money.euros(90.00), 6, true);

        List<AccommodationResponse> responses = AccommodationRestMapper
                .toResponseList(List.of(accommodation1, accommodation2, accommodation3));

        assertThat(responses).hasSize(3);
        assertThat(responses).allMatch(r -> r.accommodationTypeId().equals((short) 1));
        assertThat(responses).allMatch(r -> r.accommodationTypeName().equals("Casa Rural"));
    }
}