package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.DateRange;
import com.aogdev.rural.domain.valueobject.Money;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.ReservationJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.ReservationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationPersistenceAdapter Tests")
class ReservationPersistenceAdapterTest {

    @Mock
    private ReservationJpaRepository repository;

    @InjectMocks
    private ReservationPersistenceAdapter adapter;

    private Reservation testReservation;
    private ReservationJpaEntity testEntity;

    @BeforeEach
    void setUp() {
        AccommodationType testAccommodationType = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo"
        );

        testReservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.of(2025, 11, 15),
                "Test reservation"
        );

        testEntity = ReservationJpaEntity.builder()
                .id(1L)
                .accommodationId(100L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 1))
                .endDate(LocalDate.of(2025, 12, 5))
                .bedsReserved(2)
                .totalPrice(new BigDecimal("400.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.of(2025, 11, 15))
                .notes("Test reservation")
                .build();
    }

    @Test
    @DisplayName("Should save reservation successfully")
    void shouldSaveReservationSuccessfully() {
        when(repository.save(any(ReservationJpaEntity.class))).thenReturn(testEntity);

        Reservation result = adapter.save(testReservation);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.accommodationId()).isEqualTo(100L);
        assertThat(result.adminId()).isEqualTo(200L);
        assertThat(result.bedsReserved()).isEqualTo(2);
        assertThat(result.totalPrice().value()).isEqualByComparingTo(new BigDecimal("400.00"));

        verify(repository).save(any(ReservationJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle save error gracefully")
    void shouldHandleSaveErrorGracefully() {
        when(repository.save(any(ReservationJpaEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> adapter.save(testReservation))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to save reservation");

        verify(repository).save(any(ReservationJpaEntity.class));
    }

    @Test
    @DisplayName("Should load reservation by id successfully")
    void shouldLoadReservationByIdSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));

        Optional<Reservation> result = adapter.loadById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().accommodationId()).isEqualTo(100L);
        assertThat(result.get().adminId()).isEqualTo(200L);

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when reservation not found by id")
    void shouldReturnEmptyWhenReservationNotFoundById() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Reservation> result = adapter.loadById(999L);

        assertThat(result).isEmpty();
        verify(repository).findById(999L);
    }

    @Test
    @DisplayName("Should delete reservation by id successfully")
    void shouldDeleteReservationByIdSuccessfully() {
        doNothing().when(repository).deleteById(1L);

        assertThatCode(() -> adapter.deleteById(1L)).doesNotThrowAnyException();

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle delete error gracefully")
    void shouldHandleDeleteErrorGracefully() {
        doThrow(new RuntimeException("Database error")).when(repository).deleteById(1L);

        assertThatThrownBy(() -> adapter.deleteById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to delete reservation");

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Should find all reservations successfully")
    void shouldFindAllReservationsSuccessfully() {
        ReservationJpaEntity entity2 = ReservationJpaEntity.builder()
                .id(2L)
                .accommodationId(101L)
                .adminId(201L)
                .startDate(LocalDate.of(2025, 12, 10))
                .endDate(LocalDate.of(2025, 12, 15))
                .bedsReserved(3)
                .totalPrice(new BigDecimal("600.00"))
                .currency("EUR")
                .paid(true)
                .bookingDate(LocalDate.of(2025, 11, 20))
                .notes("Another reservation")
                .build();

        when(repository.findAll()).thenReturn(List.of(testEntity, entity2));

        List<Reservation> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no reservations exist")
    void shouldReturnEmptyListWhenNoReservationsExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<Reservation> result = adapter.findAll();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should find reservations by accommodation id successfully")
    void shouldFindReservationsByAccommodationIdSuccessfully() {
        when(repository.findByAccommodationId(100L)).thenReturn(List.of(testEntity));

        List<Reservation> result = adapter.findByAccommodationId(100L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().accommodationId()).isEqualTo(100L);

        verify(repository).findByAccommodationId(100L);
    }

    @Test
    @DisplayName("Should return empty list when no reservations for accommodation")
    void shouldReturnEmptyListWhenNoReservationsForAccommodation() {
        when(repository.findByAccommodationId(999L)).thenReturn(List.of());

        List<Reservation> result = adapter.findByAccommodationId(999L);

        assertThat(result).isEmpty();
        verify(repository).findByAccommodationId(999L);
    }

    @Test
    @DisplayName("Should find reservations by admin id successfully")
    void shouldFindReservationsByAdminIdSuccessfully() {
        when(repository.findByAdminId(200L)).thenReturn(List.of(testEntity));

        List<Reservation> result = adapter.findByAdminId(200L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().adminId()).isEqualTo(200L);

        verify(repository).findByAdminId(200L);
    }

    @Test
    @DisplayName("Should return empty list when no reservations for admin")
    void shouldReturnEmptyListWhenNoReservationsForAdmin() {
        when(repository.findByAdminId(999L)).thenReturn(List.of());

        List<Reservation> result = adapter.findByAdminId(999L);

        assertThat(result).isEmpty();
        verify(repository).findByAdminId(999L);
    }

    @Test
    @DisplayName("Should find unpaid reservations successfully")
    void shouldFindUnpaidReservationsSuccessfully() {
        when(repository.findByPaidFalse()).thenReturn(List.of(testEntity));

        List<Reservation> result = adapter.findUnpaid();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().isPaid()).isFalse();

        verify(repository).findByPaidFalse();
    }

    @Test
    @DisplayName("Should return empty list when no unpaid reservations")
    void shouldReturnEmptyListWhenNoUnpaidReservations() {
        when(repository.findByPaidFalse()).thenReturn(List.of());

        List<Reservation> result = adapter.findUnpaid();

        assertThat(result).isEmpty();
        verify(repository).findByPaidFalse();
    }

    @Test
    @DisplayName("Should find reservations by date range successfully")
    void shouldFindReservationsByDateRangeSuccessfully() {
        LocalDate startDate = LocalDate.of(2025, 12, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        when(repository.findByDateRangeOverlap(startDate, endDate))
                .thenReturn(List.of(testEntity));

        List<Reservation> result = adapter.findByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().dateRange().startDate()).isEqualTo(LocalDate.of(2025, 12, 1));

        verify(repository).findByDateRangeOverlap(startDate, endDate);
    }

    @Test
    @DisplayName("Should return empty list when no reservations in date range")
    void shouldReturnEmptyListWhenNoReservationsInDateRange() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);

        when(repository.findByDateRangeOverlap(startDate, endDate)).thenReturn(List.of());

        List<Reservation> result = adapter.findByDateRange(startDate, endDate);

        assertThat(result).isEmpty();
        verify(repository).findByDateRangeOverlap(startDate, endDate);
    }

    @Test
    @DisplayName("Should find overlapping reservations successfully")
    void shouldFindOverlappingReservationsSuccessfully() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7)
        );

        when(repository.findOverlappingReservations(
                100L,
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7),
                null
        )).thenReturn(List.of(testEntity));

        List<Reservation> result = adapter.findOverlapping(100L, dateRange, null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().accommodationId()).isEqualTo(100L);

        verify(repository).findOverlappingReservations(
                100L,
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7),
                null
        );
    }

    @Test
    @DisplayName("Should find overlapping reservations excluding specific id")
    void shouldFindOverlappingReservationsExcludingSpecificId() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7)
        );

        when(repository.findOverlappingReservations(
                100L,
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7),
                5L
        )).thenReturn(List.of(testEntity));

        List<Reservation> result = adapter.findOverlapping(100L, dateRange, 5L);

        assertThat(result).hasSize(1);

        verify(repository).findOverlappingReservations(
                100L,
                LocalDate.of(2025, 12, 3),
                LocalDate.of(2025, 12, 7),
                5L
        );
    }

    @Test
    @DisplayName("Should return empty list when no overlapping reservations")
    void shouldReturnEmptyListWhenNoOverlappingReservations() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2025, 12, 20),
                LocalDate.of(2025, 12, 25)
        );

        when(repository.findOverlappingReservations(
                100L,
                LocalDate.of(2025, 12, 20),
                LocalDate.of(2025, 12, 25),
                null
        )).thenReturn(List.of());

        List<Reservation> result = adapter.findOverlapping(100L, dateRange, null);

        assertThat(result).isEmpty();

        verify(repository).findOverlappingReservations(
                100L,
                LocalDate.of(2025, 12, 20),
                LocalDate.of(2025, 12, 25),
                null
        );
    }

    @Test
    @DisplayName("Should save reservation with null id")
    void shouldSaveReservationWithNullId() {
        Reservation newReservation = new Reservation(
                null,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "New reservation"
        );

        when(repository.save(any(ReservationJpaEntity.class))).thenReturn(testEntity);

        Reservation result = adapter.save(newReservation);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        verify(repository).save(any(ReservationJpaEntity.class));
    }

    @Test
    @DisplayName("Should save reservation with null notes")
    void shouldSaveReservationWithNullNotes() {
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

        ReservationJpaEntity entityWithNullNotes = ReservationJpaEntity.builder()
                .id(1L)
                .accommodationId(100L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 1))
                .endDate(LocalDate.of(2025, 12, 5))
                .bedsReserved(2)
                .totalPrice(new BigDecimal("400.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes(null)
                .build();

        when(repository.save(any(ReservationJpaEntity.class))).thenReturn(entityWithNullNotes);

        Reservation result = adapter.save(reservation);

        assertThat(result).isNotNull();
        assertThat(result.notes()).isNull();

        verify(repository).save(any(ReservationJpaEntity.class));
    }

    @Test
    @DisplayName("Should save paid reservation")
    void shouldSavePaidReservation() {
        Reservation paidReservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                true,
                LocalDate.of(2025, 11, 15),
                "Paid reservation"
        );

        ReservationJpaEntity paidEntity = ReservationJpaEntity.builder()
                .id(1L)
                .accommodationId(100L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 1))
                .endDate(LocalDate.of(2025, 12, 5))
                .bedsReserved(2)
                .totalPrice(new BigDecimal("400.00"))
                .currency("EUR")
                .paid(true)
                .bookingDate(LocalDate.of(2025, 11, 15))
                .notes("Paid reservation")
                .build();

        when(repository.save(any(ReservationJpaEntity.class))).thenReturn(paidEntity);

        Reservation result = adapter.save(paidReservation);

        assertThat(result).isNotNull();
        assertThat(result.isPaid()).isTrue();

        verify(repository).save(any(ReservationJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle multiple reservations for same accommodation")
    void shouldHandleMultipleReservationsForSameAccommodation() {
        ReservationJpaEntity entity2 = ReservationJpaEntity.builder()
                .id(2L)
                .accommodationId(100L)
                .adminId(201L)
                .startDate(LocalDate.of(2025, 12, 10))
                .endDate(LocalDate.of(2025, 12, 15))
                .bedsReserved(3)
                .totalPrice(new BigDecimal("600.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes("Second reservation")
                .build();

        when(repository.findByAccommodationId(100L)).thenReturn(List.of(testEntity, entity2));

        List<Reservation> result = adapter.findByAccommodationId(100L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.accommodationId().equals(100L));

        verify(repository).findByAccommodationId(100L);
    }

    @Test
    @DisplayName("Should handle multiple reservations for same admin")
    void shouldHandleMultipleReservationsForSameAdmin() {
        ReservationJpaEntity entity2 = ReservationJpaEntity.builder()
                .id(2L)
                .accommodationId(101L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 10))
                .endDate(LocalDate.of(2025, 12, 15))
                .bedsReserved(3)
                .totalPrice(new BigDecimal("600.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes("Second reservation")
                .build();

        when(repository.findByAdminId(200L)).thenReturn(List.of(testEntity, entity2));

        List<Reservation> result = adapter.findByAdminId(200L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.adminId().equals(200L));

        verify(repository).findByAdminId(200L);
    }

    @Test
    @DisplayName("Should preserve all reservation fields when saving")
    void shouldPreserveAllReservationFieldsWhenSaving() {
        when(repository.save(any(ReservationJpaEntity.class))).thenReturn(testEntity);

        Reservation result = adapter.save(testReservation);

        assertThat(result.id()).isEqualTo(testReservation.id());
        assertThat(result.accommodationId()).isEqualTo(testReservation.accommodationId());
        assertThat(result.adminId()).isEqualTo(testReservation.adminId());
        assertThat(result.dateRange().startDate()).isEqualTo(testReservation.dateRange().startDate());
        assertThat(result.dateRange().endDate()).isEqualTo(testReservation.dateRange().endDate());
        assertThat(result.bedsReserved()).isEqualTo(testReservation.bedsReserved());
        assertThat(result.totalPrice().value()).isEqualTo(testReservation.totalPrice().value());
        assertThat(result.totalPrice().currency()).isEqualTo(testReservation.totalPrice().currency());
        assertThat(result.paid()).isEqualTo(testReservation.paid());
        assertThat(result.bookingDate()).isEqualTo(testReservation.bookingDate());
        assertThat(result.notes()).isEqualTo(testReservation.notes());

        verify(repository).save(any(ReservationJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle reservations with different bed capacities")
    void shouldHandleReservationsWithDifferentBedCapacities() {
        ReservationJpaEntity entity1Bed = ReservationJpaEntity.builder()
                .id(2L)
                .accommodationId(101L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 10))
                .endDate(LocalDate.of(2025, 12, 15))
                .bedsReserved(1)
                .totalPrice(new BigDecimal("250.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes("Single bed")
                .build();

        ReservationJpaEntity entity10Beds = ReservationJpaEntity.builder()
                .id(3L)
                .accommodationId(102L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 10))
                .endDate(LocalDate.of(2025, 12, 15))
                .bedsReserved(10)
                .totalPrice(new BigDecimal("2500.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes("Group booking")
                .build();

        when(repository.findAll()).thenReturn(List.of(entity1Bed, entity10Beds));

        List<Reservation> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).bedsReserved()).isEqualTo(1);
        assertThat(result.get(1).bedsReserved()).isEqualTo(10);

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should handle reservations with different prices")
    void shouldHandleReservationsWithDifferentPrices() {
        ReservationJpaEntity lowPrice = ReservationJpaEntity.builder()
                .id(2L)
                .accommodationId(101L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 10))
                .endDate(LocalDate.of(2025, 12, 11))
                .bedsReserved(1)
                .totalPrice(new BigDecimal("50.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes("Budget")
                .build();

        ReservationJpaEntity highPrice = ReservationJpaEntity.builder()
                .id(3L)
                .accommodationId(102L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 10))
                .endDate(LocalDate.of(2025, 12, 17))
                .bedsReserved(8)
                .totalPrice(new BigDecimal("5000.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes("Luxury")
                .build();

        when(repository.findAll()).thenReturn(List.of(lowPrice, highPrice));

        List<Reservation> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).totalPrice().value()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(result.get(1).totalPrice().value()).isEqualByComparingTo(new BigDecimal("5000.00"));

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should handle reservations across month boundaries")
    void shouldHandleReservationsAcrossMonthBoundaries() {
        ReservationJpaEntity crossMonth = ReservationJpaEntity.builder()
                .id(2L)
                .accommodationId(100L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 11, 28))
                .endDate(LocalDate.of(2025, 12, 3))
                .bedsReserved(2)
                .totalPrice(new BigDecimal("500.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes("Cross-month")
                .build();

        when(repository.findById(2L)).thenReturn(Optional.of(crossMonth));

        Optional<Reservation> result = adapter.loadById(2L);

        assertThat(result).isPresent();
        assertThat(result.get().dateRange().startDate()).isEqualTo(LocalDate.of(2025, 11, 28));
        assertThat(result.get().dateRange().endDate()).isEqualTo(LocalDate.of(2025, 12, 3));

        verify(repository).findById(2L);
    }

    @Test
    @DisplayName("Should handle reservations across year boundaries")
    void shouldHandleReservationsAcrossYearBoundaries() {
        ReservationJpaEntity crossYear = ReservationJpaEntity.builder()
                .id(2L)
                .accommodationId(100L)
                .adminId(200L)
                .startDate(LocalDate.of(2025, 12, 30))
                .endDate(LocalDate.of(2026, 1, 3))
                .bedsReserved(2)
                .totalPrice(new BigDecimal("400.00"))
                        .currency("EUR")
                        .paid(false)
                        .bookingDate(LocalDate.now())
                        .notes("New Year")
                        .build();

        when(repository.findById(2L)).thenReturn(Optional.of(crossYear));

        Optional<Reservation> result = adapter.loadById(2L);

        assertThat(result).isPresent();
        assertThat(result.get().dateRange().startDate()).isEqualTo(LocalDate.of(2025, 12, 30));
        assertThat(result.get().dateRange().endDate()).isEqualTo(LocalDate.of(2026, 1, 3));

        verify(repository).findById(2L);
    }
}