package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.AccommodationTypeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AccommodationTypeRestMapper Tests")
class AccommodationTypeRestMapperTest {

    @Test
    @DisplayName("Should map AccommodationType to AccommodationTypeResponse")
    void shouldMapAccommodationTypeToResponse() {
        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo en casa rural tradicional"
        );

        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        assertThat(response.id()).isEqualTo((short) 1);
        assertThat(response.name()).isEqualTo("Casa Rural");
        assertThat(response.description()).isEqualTo("Alojamiento completo en casa rural tradicional");
    }

    @Test
    @DisplayName("Should map AccommodationType with null description to response")
    void shouldMapAccommodationTypeWithNullDescriptionToResponse() {
        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Apartamento",
                null
        );

        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        assertThat(response.id()).isEqualTo((short) 1);
        assertThat(response.name()).isEqualTo("Apartamento");
        assertThat(response.description()).isNull();
    }

    @Test
    @DisplayName("Should map AccommodationType with null id to response")
    void shouldMapAccommodationTypeWithNullIdToResponse() {
        AccommodationType accommodationType = new AccommodationType(
                null,
                "Bungalow",
                "Bungalow independiente"
        );

        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        assertThat(response.id()).isNull();
        assertThat(response.name()).isEqualTo("Bungalow");
        assertThat(response.description()).isEqualTo("Bungalow independiente");
    }

    @Test
    @DisplayName("Should map list of AccommodationTypes to list of AccommodationTypeResponses")
    void shouldMapAccommodationTypeListToResponseList() {
        AccommodationType type1 = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo en casa rural tradicional"
        );

        AccommodationType type2 = new AccommodationType(
                (short) 2,
                "Apartamento",
                "Apartamento turístico"
        );

        AccommodationType type3 = new AccommodationType(
                (short) 3,
                "Bungalow",
                "Bungalow con jardín"
        );

        List<AccommodationTypeResponse> responses = AccommodationTypeRestMapper
                .toResponseList(List.of(type1, type2, type3));

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).id()).isEqualTo((short) 1);
        assertThat(responses.get(0).name()).isEqualTo("Casa Rural");
        assertThat(responses.get(1).id()).isEqualTo((short) 2);
        assertThat(responses.get(1).name()).isEqualTo("Apartamento");
        assertThat(responses.get(2).id()).isEqualTo((short) 3);
        assertThat(responses.get(2).name()).isEqualTo("Bungalow");
    }

    @Test
    @DisplayName("Should map empty list to empty response list")
    void shouldMapEmptyListToEmptyResponseList() {
        List<AccommodationTypeResponse> responses = AccommodationTypeRestMapper.toResponseList(List.of());

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Should preserve order when mapping list")
    void shouldPreserveOrderWhenMappingList() {
        AccommodationType type1 = new AccommodationType((short) 3, "C", "Third");
        AccommodationType type2 = new AccommodationType((short) 1, "A", "First");
        AccommodationType type3 = new AccommodationType((short) 2, "B", "Second");

        List<AccommodationTypeResponse> responses = AccommodationTypeRestMapper
                .toResponseList(List.of(type1, type2, type3));

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).id()).isEqualTo((short) 3);
        assertThat(responses.get(1).id()).isEqualTo((short) 1);
        assertThat(responses.get(2).id()).isEqualTo((short) 2);
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void shouldHandleSpecialCharactersInName() {
        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Casa «El Olivo»",
                "Con carácter especial"
        );

        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        assertThat(response.name()).isEqualTo("Casa «El Olivo»");
        assertThat(response.description()).isEqualTo("Con carácter especial");
    }

    @Test
    @DisplayName("Should handle very long descriptions")
    void shouldHandleVeryLongDescriptions() {
        String longDescription = "A".repeat(1000);
        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Casa Grande",
                longDescription
        );

        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        assertThat(response.description()).hasSize(1000);
    }

    @Test
    @DisplayName("Should map list with mixed null descriptions")
    void shouldMapListWithMixedNullDescriptions() {
        AccommodationType type1 = new AccommodationType((short) 1, "Casa Rural", "Con descripción");
        AccommodationType type2 = new AccommodationType((short) 2, "Apartamento", null);
        AccommodationType type3 = new AccommodationType((short) 3, "Bungalow", "Otra descripción");

        List<AccommodationTypeResponse> responses = AccommodationTypeRestMapper
                .toResponseList(List.of(type1, type2, type3));

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).description()).isEqualTo("Con descripción");
        assertThat(responses.get(1).description()).isNull();
        assertThat(responses.get(2).description()).isEqualTo("Otra descripción");
    }

    @Test
    @DisplayName("Should handle single element list")
    void shouldHandleSingleElementList() {
        AccommodationType type = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Única casa"
        );

        List<AccommodationTypeResponse> responses = AccommodationTypeRestMapper.toResponseList(List.of(type));

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo((short) 1);
        assertThat(responses.getFirst().name()).isEqualTo("Casa Rural");
    }

    @Test
    @DisplayName("Should map all fields correctly")
    void shouldMapAllFieldsCorrectly() {
        AccommodationType accommodationType = new AccommodationType(
                (short) 42,
                "Villa de Lujo",
                "Una villa espectacular con todas las comodidades"
        );

        AccommodationTypeResponse response = AccommodationTypeRestMapper.toResponse(accommodationType);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo((short) 42);
        assertThat(response.name()).isEqualTo("Villa de Lujo");
        assertThat(response.description()).isEqualTo("Una villa espectacular con todas las comodidades");
    }
}