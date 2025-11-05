package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AccommodationType Domain Model Tests")
class AccommodationTypeTest {

    @Test
    @DisplayName("Should create valid accommodation type")
    void shouldCreateValidAccommodationType() {
        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo en casa rural tradicional"
        );

        assertThat(accommodationType.id()).isEqualTo((short) 1);
        assertThat(accommodationType.name()).isEqualTo("Casa Rural");
        assertThat(accommodationType.description())
                .isEqualTo("Alojamiento completo en casa rural tradicional");
    }

    @Test
    @DisplayName("Should create accommodation type with null description")
    void shouldCreateAccommodationTypeWithNullDescription() {
        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Apartamento",
                null
        );

        assertThat(accommodationType.id()).isEqualTo((short) 1);
        assertThat(accommodationType.name()).isEqualTo("Apartamento");
        assertThat(accommodationType.description()).isNull();
    }

    @Test
    @DisplayName("Should create accommodation type with null id")
    void shouldCreateAccommodationTypeWithNullId() {
        AccommodationType accommodationType = new AccommodationType(
                null,
                "Habitación",
                "Habitación individual con baño privado"
        );

        assertThat(accommodationType.id()).isNull();
        assertThat(accommodationType.name()).isEqualTo("Habitación");
        assertThat(accommodationType.description()).isEqualTo("Habitación individual con baño privado");
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThatThrownBy(() -> new AccommodationType(
                (short) 1,
                null,
                "Descripción"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("name cannot be blank");
    }

    @Test
    @DisplayName("Should throw exception when name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThatThrownBy(() -> new AccommodationType(
                (short) 1,
                "   ",
                "Descripción"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("name cannot be blank");
    }

    @Test
    @DisplayName("Should throw exception when name is empty")
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThatThrownBy(() -> new AccommodationType(
                (short) 1,
                "",
                "Descripción"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("name cannot be blank");
    }

    @Test
    @DisplayName("Should preserve immutability")
    void shouldPreserveImmutability() {
        AccommodationType original = new AccommodationType(
                (short) 1,
                "Bungalow",
                "Bungalow independiente con jardín"
        );

        AccommodationType copy = new AccommodationType(
                original.id(),
                original.name(),
                original.description()
        );

        assertThat(copy).isNotSameAs(original);
        assertThat(copy.id()).isEqualTo(original.id());
        assertThat(copy.name()).isEqualTo(original.name());
        assertThat(copy.description()).isEqualTo(original.description());
    }

    @Test
    @DisplayName("Should handle different id values")
    void shouldHandleDifferentIdValues() {
        AccommodationType type1 = new AccommodationType(
                (short) 1,
                "Tipo 1",
                "Descripción 1"
        );

        AccommodationType type2 = new AccommodationType(
                (short) 2,
                "Tipo 2",
                "Descripción 2"
        );

        assertThat(type1.id()).isNotEqualTo(type2.id());
    }

    @Test
    @DisplayName("Should accept very long description")
    void shouldAcceptVeryLongDescription() {
        String longDescription = "A".repeat(1000);

        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Casa Grande",
                longDescription
        );

        assertThat(accommodationType.description()).hasSize(1000);
    }

    @Test
    @DisplayName("Should accept special characters in name")
    void shouldAcceptSpecialCharactersInName() {
        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Casa Rural «El Olivo»",
                "Casa con carácter especial"
        );

        assertThat(accommodationType.name()).isEqualTo("Casa Rural «El Olivo»");
    }
}