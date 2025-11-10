package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobject.DateRange;
import com.aogdev.rural.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Reservation Domain Model Tests")
class ReservationTest {

    @Test
    @DisplayName("Should create valid reservation")
    void shouldCreateValidReservation() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );
        Money totalPrice = Money.euros(400.00);
        LocalDate bookingDate = LocalDate.now();

        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                dateRange,
                2,
                totalPrice,
                false,
                bookingDate,
                "Test reservation"
        );

        assertThat(reservation.id()).isEqualTo(1L);
        assertThat(reservation.accommodationId()).isEqualTo(100L);
        assertThat(reservation.adminId()).isEqualTo(200L);
        assertThat(reservation.dateRange()).isEqualTo(dateRange);
        assertThat(reservation.bedsReserved()).isEqualTo(2);
        assertThat(reservation.totalPrice()).isEqualTo(totalPrice);
        assertThat(reservation.isPaid()).isFalse();
        assertThat(reservation.bookingDate()).isEqualTo(bookingDate);
        assertThat(reservation.notes()).isEqualTo("Test reservation");
    }

    @Test
    @DisplayName("Should default to unpaid when paid is null")
    void shouldDefaultToUnpaidWhenNull() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                null,
                LocalDate.now(),
                "Test"
        );

        assertThat(reservation.paid()).isFalse();
        assertThat(reservation.isPaid()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when accommodation ID is null")
    void shouldThrowExceptionWhenAccommodationIdIsNull() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                null,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("accommodation ID cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when admin ID is null")
    void shouldThrowExceptionWhenAdminIdIsNull() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                100L,
                null,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("admin ID cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when date range is null")
    void shouldThrowExceptionWhenDateRangeIsNull() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                100L,
                200L,
                null,
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("date range cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when beds reserved is null")
    void shouldThrowExceptionWhenBedsReservedIsNull() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                null,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("beds reserved must be positive");
    }

    @Test
    @DisplayName("Should throw exception when beds reserved is zero")
    void shouldThrowExceptionWhenBedsReservedIsZero() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                0,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("beds reserved must be positive");
    }

    @Test
    @DisplayName("Should throw exception when beds reserved is negative")
    void shouldThrowExceptionWhenBedsReservedIsNegative() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                -1,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("beds reserved must be positive");
    }

    @Test
    @DisplayName("Should throw exception when total price is null")
    void shouldThrowExceptionWhenTotalPriceIsNull() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                null,
                false,
                LocalDate.now(),
                "Test"
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("total price cannot be null");
    }

    @Test
    @DisplayName("Should calculate nights correctly")
    void shouldCalculateNightsCorrectly() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        );

        assertThat(reservation.nights()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should mark reservation as paid")
    void shouldMarkReservationAsPaid() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        );

        Reservation paidReservation = reservation.markAsPaid();

        assertThat(paidReservation.isPaid()).isTrue();
        assertThat(reservation.isPaid()).isFalse();
    }

    @Test
    @DisplayName("Should preserve immutability when marking as paid")
    void shouldPreserveImmutabilityWhenMarkingAsPaid() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );
        LocalDate bookingDate = LocalDate.now();

        Reservation original = new Reservation(
                1L,
                100L,
                200L,
                dateRange,
                2,
                Money.euros(400.00),
                false,
                bookingDate,
                "Test"
        );

        Reservation paid = original.markAsPaid();

        assertThat(paid).isNotSameAs(original);
        assertThat(paid.id()).isEqualTo(original.id());
        assertThat(paid.accommodationId()).isEqualTo(original.accommodationId());
        assertThat(paid.adminId()).isEqualTo(original.adminId());
        assertThat(paid.dateRange()).isEqualTo(original.dateRange());
        assertThat(paid.bedsReserved()).isEqualTo(original.bedsReserved());
        assertThat(paid.totalPrice()).isEqualTo(original.totalPrice());
        assertThat(paid.bookingDate()).isEqualTo(original.bookingDate());
        assertThat(paid.notes()).isEqualTo(original.notes());
    }

    @Test
    @DisplayName("Should detect overlapping date ranges")
    void shouldDetectOverlappingDateRanges() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        );

        DateRange overlapping = new DateRange(
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7)
        );

        assertThat(reservation.overlaps(overlapping)).isTrue();
    }

    @Test
    @DisplayName("Should not detect non-overlapping date ranges")
    void shouldNotDetectNonOverlappingDateRanges() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        );

        DateRange nonOverlapping = new DateRange(
                LocalDate.of(2025, 12, 6),
                LocalDate.of(2025, 12, 10)
        );

        assertThat(reservation.overlaps(nonOverlapping)).isFalse();
    }

    @Test
    @DisplayName("Should detect adjacent date ranges as non-overlapping")
    void shouldDetectAdjacentDateRangesAsNonOverlapping() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        );

        DateRange adjacent = new DateRange(
                LocalDate.of(2025, 12, 5),
                LocalDate.of(2025, 12, 10)
        );

        assertThat(reservation.overlaps(adjacent)).isFalse();
    }

    @Test
    @DisplayName("Should allow null notes")
    void shouldAllowNullNotes() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                null
        );

        assertThat(reservation.notes()).isNull();
    }
}