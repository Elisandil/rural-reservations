package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Accommodation Domain Model Tests")
class AccommodationTest {

    @Test
    @DisplayName("Should create valid accommodation")
    void shouldCreateValidAccommodation() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");
        Money price = Money.euros(85.00);

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                price,
                4,
                true
        );

        assertThat(accommodation.id()).isEqualTo(1L);
        assertThat(accommodation.accommodationType()).isEqualTo(type);
        assertThat(accommodation.name()).isEqualTo("Casa del Bosque");
        assertThat(accommodation.pricePerNight()).isEqualTo(price);
        assertThat(accommodation.bedCapacity()).isEqualTo(4);
        assertThat(accommodation.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should default to active when active is null")
    void shouldDefaultToActiveWhenNull() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                null
        );

        assertThat(accommodation.active()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");

        assertThatThrownBy(() -> new Accommodation(
                1L,
                type,
                null,
                Money.euros(85.00),
                4,
                true
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("name cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");

        assertThatThrownBy(() -> new Accommodation(
                1L,
                type,
                "   ",
                Money.euros(85.00),
                4,
                true
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("name cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when price per night is null")
    void shouldThrowExceptionWhenPricePerNightIsNull() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");

        assertThatThrownBy(() -> new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                null,
                4,
                true
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("price per night cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when bed capacity is null")
    void shouldThrowExceptionWhenBedCapacityIsNull() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");

        assertThatThrownBy(() -> new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                null,
                true
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("bed capacity must be positive");
    }

    @Test
    @DisplayName("Should throw exception when bed capacity is zero")
    void shouldThrowExceptionWhenBedCapacityIsZero() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");

        assertThatThrownBy(() -> new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                0,
                true
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("bed capacity must be positive");
    }

    @Test
    @DisplayName("Should throw exception when bed capacity is negative")
    void shouldThrowExceptionWhenBedCapacityIsNegative() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");

        assertThatThrownBy(() -> new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                -1,
                true
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("bed capacity must be positive");
    }

    @Test
    @DisplayName("Should return true when accommodation is active")
    void shouldReturnTrueWhenAccommodationIsActive() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");
        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        assertThat(accommodation.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return false when accommodation is inactive")
    void shouldReturnFalseWhenAccommodationIsInactive() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");
        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                false
        );

        assertThat(accommodation.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should have capacity for requested beds")
    void shouldHaveCapacityForRequestedBeds() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");
        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        assertThat(accommodation.hasCapacityFor(3)).isTrue();
        assertThat(accommodation.hasCapacityFor(4)).isTrue();
    }

    @Test
    @DisplayName("Should not have capacity for requested beds")
    void shouldNotHaveCapacityForRequestedBeds() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");
        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        assertThat(accommodation.hasCapacityFor(5)).isFalse();
    }

    @Test
    @DisplayName("Should handle null bed capacity in hasCapacityFor")
    void shouldHandleNullBedCapacityInHasCapacityFor() {
        AccommodationType type = new AccommodationType((short) 1, "Casa Rural", "Casa completa");

        Accommodation accommodation = new Accommodation(
                1L,
                type,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        assertThat(accommodation.hasCapacityFor(10)).isFalse();
    }
}