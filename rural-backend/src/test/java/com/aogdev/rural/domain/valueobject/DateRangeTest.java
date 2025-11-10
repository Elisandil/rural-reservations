package com.aogdev.rural.domain.valueobject;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DateRange Value Object Tests")
class DateRangeTest {

    @Test
    @DisplayName("Should create valid date range")
    void shouldCreateValidDateRange() {
        LocalDate start = LocalDate.of(2025, 12, 1);
        LocalDate end = LocalDate.of(2025, 12, 5);

        DateRange dateRange = new DateRange(start, end);

        assertThat(dateRange.startDate()).isEqualTo(start);
        assertThat(dateRange.endDate()).isEqualTo(end);
    }

    @Test
    @DisplayName("Should throw exception when start date is null")
    void shouldThrowExceptionWhenStartDateIsNull() {
        assertThatThrownBy(() -> new DateRange(null, LocalDate.of(2025, 12, 5)))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("start date and end date cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when end date is null")
    void shouldThrowExceptionWhenEndDateIsNull() {
        assertThatThrownBy(() -> new DateRange(LocalDate.of(2025, 12, 1), null))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("start date and end date cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when end date is before start date")
    void shouldThrowExceptionWhenEndDateIsBeforeStartDate() {
        assertThatThrownBy(() -> new DateRange(
                LocalDate.of(2025, 12, 5),
                LocalDate.of(2025, 12, 1)
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("end date must be after start date");
    }

    @Test
    @DisplayName("Should throw exception when end date equals start date")
    void shouldThrowExceptionWhenEndDateEqualsStartDate() {
        LocalDate sameDate = LocalDate.of(2025, 12, 1);

        assertThatThrownBy(() -> new DateRange(sameDate, sameDate))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("end date must be after start date");
    }

    @Test
    @DisplayName("Should calculate nights correctly")
    void shouldCalculateNightsCorrectly() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );

        assertThat(dateRange.nights()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should calculate one night for consecutive days")
    void shouldCalculateOneNightForConsecutiveDays() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 2)
        );

        assertThat(dateRange.nights()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should detect overlapping ranges")
    void shouldDetectOverlappingRanges() {
        DateRange range1 = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );
        DateRange range2 = new DateRange(
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7)
        );

        assertThat(range1.overlaps(range2)).isTrue();
        assertThat(range2.overlaps(range1)).isTrue();
    }

    @Test
    @DisplayName("Should detect complete overlap")
    void shouldDetectCompleteOverlap() {
        DateRange range1 = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 10)
        );
        DateRange range2 = new DateRange(
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7)
        );

        assertThat(range1.overlaps(range2)).isTrue();
        assertThat(range2.overlaps(range1)).isTrue();
    }

    @Test
    @DisplayName("Should detect same date ranges as overlapping")
    void shouldDetectSameDateRangesAsOverlapping() {
        DateRange range1 = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );
        DateRange range2 = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );

        assertThat(range1.overlaps(range2)).isTrue();
    }

    @Test
    @DisplayName("Should not detect non-overlapping ranges")
    void shouldNotDetectNonOverlappingRanges() {
        DateRange range1 = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );
        DateRange range2 = new DateRange(
                LocalDate.of(2025, 12, 6),
                LocalDate.of(2025, 12, 10)
        );

        assertThat(range1.overlaps(range2)).isFalse();
        assertThat(range2.overlaps(range1)).isFalse();
    }

    @Test
    @DisplayName("Should not detect adjacent ranges as overlapping")
    void shouldNotDetectAdjacentRangesAsOverlapping() {
        DateRange range1 = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );
        DateRange range2 = new DateRange(
                LocalDate.of(2025, 12, 5),
                LocalDate.of(2025, 12, 10)
        );

        assertThat(range1.overlaps(range2)).isFalse();
        assertThat(range2.overlaps(range1)).isFalse();
    }

    @Test
    @DisplayName("Should contain date within range")
    void shouldContainDateWithinRange() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );

        assertThat(dateRange.contains(LocalDate.of(2025, 12, 3))).isTrue();
    }

    @Test
    @DisplayName("Should contain start date")
    void shouldContainStartDate() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );

        assertThat(dateRange.contains(LocalDate.of(2025, 12, 1))).isTrue();
    }

    @Test
    @DisplayName("Should not contain end date")
    void shouldNotContainEndDate() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );

        assertThat(dateRange.contains(LocalDate.of(2025, 12, 5))).isFalse();
    }

    @Test
    @DisplayName("Should not contain date before range")
    void shouldNotContainDateBeforeRange() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );

        assertThat(dateRange.contains(LocalDate.of(2025, 11, 30))).isFalse();
    }

    @Test
    @DisplayName("Should not contain date after range")
    void shouldNotContainDateAfterRange() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );

        assertThat(dateRange.contains(LocalDate.of(2025, 12, 6))).isFalse();
    }

    @Test
    @DisplayName("Should handle date ranges across months")
    void shouldHandleDateRangesAcrossMonths() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 11, 28),
                LocalDate.of(2025, 12, 3)
        );

        assertThat(dateRange.nights()).isEqualTo(5);
        assertThat(dateRange.contains(LocalDate.of(2025, 12, 1))).isTrue();
    }

    @Test
    @DisplayName("Should handle date ranges across years")
    void shouldHandleDateRangesAcrossYears() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 30),
                LocalDate.of(2026, 1, 3)
        );

        assertThat(dateRange.nights()).isEqualTo(4);
        assertThat(dateRange.contains(LocalDate.of(2026, 1, 1))).isTrue();
    }
}