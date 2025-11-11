package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.reservation.CreateReservationCommand;
import com.aogdev.rural.application.port.in.reservation.UpdateReservationCommand;
import com.aogdev.rural.application.port.out.accommodation.LoadAccommodationPort;
import com.aogdev.rural.application.port.out.admin.LoadAdminPort;
import com.aogdev.rural.application.port.out.reservation.*;
import com.aogdev.rural.domain.exception.accommodation.AccommodationNotFoundException;
import com.aogdev.rural.domain.exception.admin.AdminNotFoundException;
import com.aogdev.rural.domain.exception.reservation.InsufficientCapacityException;
import com.aogdev.rural.domain.exception.reservation.ReservationNotFoundException;
import com.aogdev.rural.domain.exception.reservation.ReservationOverlapException;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.DateRange;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.Money;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService Unit Tests")
class ReservationServiceTest {

    @Mock
    private SaveReservationPort saveReservationPort;

    @Mock
    private LoadReservationPort loadReservationPort;

    @Mock
    private DeleteReservationPort deleteReservationPort;

    @Mock
    private ListReservationsPort listReservationsPort;

    @Mock
    private FindOverlappingReservationsPort findOverlappingReservationsPort;

    @Mock
    private LoadAccommodationPort loadAccommodationPort;

    @Mock
    private LoadAdminPort loadAdminPort;

    private ReservationService reservationService;

    private Accommodation testAccommodation;
    private Admin testAdmin;
    private Reservation testReservation;
    private DateRange testDateRange;
    private CreateReservationCommand testCreateCommand;
    private UpdateReservationCommand testUpdateCommand;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
                saveReservationPort,
                loadReservationPort,
                deleteReservationPort,
                listReservationsPort,
                findOverlappingReservationsPort,
                loadAccommodationPort,
                loadAdminPort
        );

        AccommodationType accommodationType = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo"
        );

        testAccommodation = new Accommodation(
                1L,
                accommodationType,
                "Casa del Bosque",
                Money.euros(100.00),
                4,
                true
        );

        testAdmin = new Admin(
                1L,
                new PersonName("Juan", "García"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                "hashedPassword",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        testDateRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5)
        );

        testReservation = new Reservation(
                1L,
                1L,
                1L,
                testDateRange,
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test reservation"
        );

        testCreateCommand = new CreateReservationCommand(
                1L,
                1L,
                testDateRange,
                2,
                "Test reservation"
        );

        testUpdateCommand = new UpdateReservationCommand(
                1L,
                1L,
                1L,
                testDateRange,
                2,
                "Updated reservation"
        );
    }

    @Test
    @DisplayName("Should create reservation successfully when all validations pass")
    void shouldCreateReservationSuccessfully() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, null))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(testReservation);

        Reservation result = reservationService.create(testCreateCommand);

        assertThat(result).isNotNull();
        assertThat(result.accommodationId()).isEqualTo(1L);
        assertThat(result.adminId()).isEqualTo(1L);
        assertThat(result.bedsReserved()).isEqualTo(2);
        assertThat(result.totalPrice().value()).isEqualByComparingTo(new BigDecimal("400.00"));
        assertThat(result.isPaid()).isFalse();

        verify(loadAccommodationPort).loadById(1L);
        verify(loadAdminPort).loadById(1L);
        verify(findOverlappingReservationsPort).findOverlapping(1L, testDateRange, null);
        verify(saveReservationPort).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw AccommodationNotFoundException when accommodation does not exist")
    void shouldThrowExceptionWhenAccommodationNotFound() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(testCreateCommand))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessageContaining("1");

        verify(loadAccommodationPort).loadById(1L);
        verify(loadAdminPort, never()).loadById(any());
        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw AdminNotFoundException when admin does not exist")
    void shouldThrowExceptionWhenAdminNotFound() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(testCreateCommand))
                .isInstanceOf(AdminNotFoundException.class)
                .hasMessageContaining("1");

        verify(loadAccommodationPort).loadById(1L);
        verify(loadAdminPort).loadById(1L);
        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw InsufficientCapacityException when beds exceed capacity")
    void shouldThrowExceptionWhenInsufficientCapacity() {
        CreateReservationCommand command = new CreateReservationCommand(
                1L,
                1L,
                testDateRange,
                10,
                "Test"
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));

        assertThatThrownBy(() -> reservationService.create(command))
                .isInstanceOf(InsufficientCapacityException.class)
                .hasMessageContaining("10")
                .hasMessageContaining("4");

        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw ReservationOverlapException when dates overlap")
    void shouldThrowExceptionWhenDatesOverlap() {
        Reservation existingReservation = new Reservation(
                2L,
                1L,
                1L,
                new DateRange(LocalDate.of(2025, 12, 3), LocalDate.of(2025, 12, 7)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Existing"
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, null))
                .thenReturn(List.of(existingReservation));

        assertThatThrownBy(() -> reservationService.create(testCreateCommand))
                .isInstanceOf(ReservationOverlapException.class)
                .hasMessageContaining("1");

        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should calculate total price correctly when creating")
    void shouldCalculateTotalPriceCorrectly() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, null))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(testReservation);

        reservationService.create(testCreateCommand);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(saveReservationPort).save(captor.capture());

        Reservation savedReservation = captor.getValue();
        assertThat(savedReservation.totalPrice().value())
                .isEqualByComparingTo(new BigDecimal("400.00"));
        assertThat(savedReservation.nights()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should save reservation with null id and unpaid status when creating")
    void shouldSaveReservationWithNullIdAndUnpaidStatus() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, null))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(testReservation);

        reservationService.create(testCreateCommand);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(saveReservationPort).save(captor.capture());

        Reservation savedReservation = captor.getValue();
        assertThat(savedReservation.id()).isNull();
        assertThat(savedReservation.paid()).isFalse();
        assertThat(savedReservation.bookingDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Should get reservation by id successfully")
    void shouldGetReservationByIdSuccessfully() {
        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));

        Reservation result = reservationService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        verify(loadReservationPort).loadById(1L);
    }

    @Test
    @DisplayName("Should throw ReservationNotFoundException when reservation not found by id")
    void shouldThrowExceptionWhenReservationNotFoundById() {
        when(loadReservationPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getById(999L))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadReservationPort).loadById(999L);
    }

    @Test
    @DisplayName("Should update reservation successfully")
    void shouldUpdateReservationSuccessfully() {
        DateRange newDateRange = new DateRange(
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 15)
        );

        UpdateReservationCommand command = new UpdateReservationCommand(
                1L,
                1L,
                1L,
                newDateRange,
                3,
                "Updated"
        );

        Reservation updatedReservation = new Reservation(
                1L,
                1L,
                1L,
                newDateRange,
                3,
                Money.euros(500.00),
                false,
                LocalDate.now(),
                "Updated"
        );

        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, newDateRange, 1L))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(updatedReservation);

        Reservation result = reservationService.update(command);

        assertThat(result).isNotNull();
        assertThat(result.bedsReserved()).isEqualTo(3);
        assertThat(result.totalPrice().value()).isEqualByComparingTo(new BigDecimal("500.00"));

        verify(loadReservationPort).loadById(1L);
        verify(loadAccommodationPort).loadById(1L);
        verify(loadAdminPort).loadById(1L);
        verify(findOverlappingReservationsPort).findOverlapping(1L, newDateRange, 1L);
        verify(saveReservationPort).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent reservation")
    void shouldThrowExceptionWhenUpdatingNonExistentReservation() {
        when(loadReservationPort.loadById(999L)).thenReturn(Optional.empty());

        UpdateReservationCommand command = new UpdateReservationCommand(
                999L,
                1L,
                1L,
                testDateRange,
                2,
                "Test"
        );

        assertThatThrownBy(() -> reservationService.update(command))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadReservationPort).loadById(999L);
        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with non-existent accommodation")
    void shouldThrowExceptionWhenUpdatingWithNonExistentAccommodation() {
        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.update(testUpdateCommand))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessageContaining("1");

        verify(loadReservationPort).loadById(1L);
        verify(loadAccommodationPort).loadById(1L);
        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with non-existent admin")
    void shouldThrowExceptionWhenUpdatingWithNonExistentAdmin() {
        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.update(testUpdateCommand))
                .isInstanceOf(AdminNotFoundException.class)
                .hasMessageContaining("1");

        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating causes insufficient capacity")
    void shouldThrowExceptionWhenUpdatingCausesInsufficientCapacity() {
        UpdateReservationCommand command = new UpdateReservationCommand(
                1L,
                1L,
                1L,
                testDateRange,
                10,
                "Test"
        );

        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));

        assertThatThrownBy(() -> reservationService.update(command))
                .isInstanceOf(InsufficientCapacityException.class)
                .hasMessageContaining("10")
                .hasMessageContaining("4");

        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating causes overlap with other reservations")
    void shouldThrowExceptionWhenUpdatingCausesOverlap() {
        Reservation otherReservation = new Reservation(
                2L,
                1L,
                1L,
                new DateRange(LocalDate.of(2025, 12, 3), LocalDate.of(2025, 12, 7)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Other"
        );

        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, 1L))
                .thenReturn(List.of(otherReservation));

        assertThatThrownBy(() -> reservationService.update(testUpdateCommand))
                .isInstanceOf(ReservationOverlapException.class)
                .hasMessageContaining("1");

        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should preserve paid status and booking date when updating")
    void shouldPreservePaidStatusAndBookingDateWhenUpdating() {
        Reservation paidReservation = new Reservation(
                1L,
                1L,
                1L,
                testDateRange,
                2,
                Money.euros(400.00),
                true,
                LocalDate.of(2025, 11, 1),
                "Test"
        );

        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(paidReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, 1L))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(paidReservation);

        reservationService.update(testUpdateCommand);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(saveReservationPort).save(captor.capture());

        Reservation savedReservation = captor.getValue();
        assertThat(savedReservation.paid()).isTrue();
        assertThat(savedReservation.bookingDate()).isEqualTo(LocalDate.of(2025, 11, 1));
    }

    @Test
    @DisplayName("Should mark reservation as paid successfully")
    void shouldMarkReservationAsPaidSuccessfully() {
        Reservation paidReservation = new Reservation(
                1L,
                1L,
                1L,
                testDateRange,
                2,
                Money.euros(400.00),
                true,
                LocalDate.now(),
                "Test"
        );

        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(paidReservation);

        reservationService.markAsPaid(1L);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(saveReservationPort).save(captor.capture());

        Reservation savedReservation = captor.getValue();
        assertThat(savedReservation.paid()).isTrue();
    }

    @Test
    @DisplayName("Should not save when marking already paid reservation")
    void shouldNotSaveWhenMarkingAlreadyPaidReservation() {
        Reservation paidReservation = new Reservation(
                1L,
                1L,
                1L,
                testDateRange,
                2,
                Money.euros(400.00),
                true,
                LocalDate.now(),
                "Test"
        );

        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(paidReservation));

        reservationService.markAsPaid(1L);

        verify(loadReservationPort).loadById(1L);
        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when marking non-existent reservation as paid")
    void shouldThrowExceptionWhenMarkingNonExistentReservationAsPaid() {
        when(loadReservationPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.markAsPaid(999L))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadReservationPort).loadById(999L);
        verify(saveReservationPort, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should cancel reservation successfully")
    void shouldCancelReservationSuccessfully() {
        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));

        reservationService.cancel(1L);

        verify(loadReservationPort).loadById(1L);
        verify(deleteReservationPort).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when cancelling non-existent reservation")
    void shouldThrowExceptionWhenCancellingNonExistentReservation() {
        when(loadReservationPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancel(999L))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadReservationPort).loadById(999L);
        verify(deleteReservationPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should list all reservations")
    void shouldListAllReservations() {
        Reservation reservation2 = new Reservation(
                2L,
                1L,
                1L,
                new DateRange(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15)),
                3,
                Money.euros(500.00),
                false,
                LocalDate.now(),
                "Test 2"
        );

        when(listReservationsPort.findAll())
                .thenReturn(List.of(testReservation, reservation2));

        List<Reservation> result = reservationService.listAll();

        assertThat(result).hasSize(2);
        assertThat(result).contains(testReservation, reservation2);
        verify(listReservationsPort).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no reservations exist")
    void shouldReturnEmptyListWhenNoReservationsExist() {
        when(listReservationsPort.findAll()).thenReturn(List.of());

        List<Reservation> result = reservationService.listAll();

        assertThat(result).isEmpty();
        verify(listReservationsPort).findAll();
    }

    @Test
    @DisplayName("Should list reservations by accommodation")
    void shouldListReservationsByAccommodation() {
        Reservation reservation2 = new Reservation(
                2L,
                1L,
                1L,
                new DateRange(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15)),
                3,
                Money.euros(500.00),
                false,
                LocalDate.now(),
                "Test 2"
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(listReservationsPort.findByAccommodationId(1L))
                .thenReturn(List.of(testReservation, reservation2));

        List<Reservation> result = reservationService.listByAccommodation(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.accommodationId().equals(1L));
        verify(loadAccommodationPort).loadById(1L);
        verify(listReservationsPort).findByAccommodationId(1L);
    }

    @Test
    @DisplayName("Should throw exception when listing by non-existent accommodation")
    void shouldThrowExceptionWhenListingByNonExistentAccommodation() {
        when(loadAccommodationPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.listByAccommodation(999L))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationPort).loadById(999L);
        verify(listReservationsPort, never()).findByAccommodationId(any());
    }

    @Test
    @DisplayName("Should list reservations by admin")
    void shouldListReservationsByAdmin() {
        Reservation reservation2 = new Reservation(
                2L,
                1L,
                1L,
                new DateRange(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15)),
                3,
                Money.euros(500.00),
                false,
                LocalDate.now(),
                "Test 2"
        );

        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(listReservationsPort.findByAdminId(1L))
                .thenReturn(List.of(testReservation, reservation2));

        List<Reservation> result = reservationService.listByAdmin(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.adminId().equals(1L));
        verify(loadAdminPort).loadById(1L);
        verify(listReservationsPort).findByAdminId(1L);
    }

    @Test
    @DisplayName("Should throw exception when listing by non-existent admin")
    void shouldThrowExceptionWhenListingByNonExistentAdmin() {
        when(loadAdminPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.listByAdmin(999L))
                .isInstanceOf(AdminNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAdminPort).loadById(999L);
        verify(listReservationsPort, never()).findByAdminId(any());
    }

    @Test
    @DisplayName("Should list reservations by date range")
    void shouldListReservationsByDateRange() {
        LocalDate start = LocalDate.of(2025, 12, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        when(listReservationsPort.findByDateRange(start, end))
                .thenReturn(List.of(testReservation));

        List<Reservation> result = reservationService.listByDateRange(start, end);

        assertThat(result).hasSize(1);
        assertThat(result).contains(testReservation);
        verify(listReservationsPort).findByDateRange(start, end);
    }

    @Test
    @DisplayName("Should throw exception when listing by date range with null start date")
    void shouldThrowExceptionWhenListingByDateRangeWithNullStartDate() {
        assertThatThrownBy(() -> reservationService
                .listByDateRange(null, LocalDate.of(2025, 12, 31)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date and end date cannot be null");

        verify(listReservationsPort, never()).findByDateRange(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when listing by date range with null end date")
    void shouldThrowExceptionWhenListingByDateRangeWithNullEndDate() {
        assertThatThrownBy(() -> reservationService.listByDateRange(LocalDate.of(2025, 12, 1), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date and end date cannot be null");

        verify(listReservationsPort, never()).findByDateRange(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when end date is not after start date")
    void shouldThrowExceptionWhenEndDateIsNotAfterStartDate() {
        LocalDate start = LocalDate.of(2025, 12, 31);
        LocalDate end = LocalDate.of(2025, 12, 1);

        assertThatThrownBy(() -> reservationService.listByDateRange(start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End date must be after start date");

        verify(listReservationsPort, never()).findByDateRange(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when end date equals start date")
    void shouldThrowExceptionWhenEndDateEqualsStartDate() {
        LocalDate sameDate = LocalDate.of(2025, 12, 1);

        assertThatThrownBy(() -> reservationService.listByDateRange(sameDate, sameDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End date must be after start date");

        verify(listReservationsPort, never()).findByDateRange(any(), any());
    }

    @Test
    @DisplayName("Should list unpaid reservations")
    void shouldListUnpaidReservations() {
        Reservation reservation2 = new Reservation(
                2L,
                1L,
                1L,
                new DateRange(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15)),
                3,
                Money.euros(500.00),
                false,
                LocalDate.now(),
                "Test 2"
        );

        when(listReservationsPort.findUnpaid())
                .thenReturn(List.of(testReservation, reservation2));

        List<Reservation> result = reservationService.listUnpaid();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> !r.isPaid());
        verify(listReservationsPort).findUnpaid();
    }

    @Test
    @DisplayName("Should return empty list when no unpaid reservations exist")
    void shouldReturnEmptyListWhenNoUnpaidReservationsExist() {
        when(listReservationsPort.findUnpaid()).thenReturn(List.of());

        List<Reservation> result = reservationService.listUnpaid();

        assertThat(result).isEmpty();
        verify(listReservationsPort).findUnpaid();
    }

    @Test
    @DisplayName("Should return empty list when listing by accommodation with no reservations")
    void shouldReturnEmptyListWhenListingByAccommodationWithNoReservations() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(listReservationsPort.findByAccommodationId(1L)).thenReturn(List.of());

        List<Reservation> result = reservationService.listByAccommodation(1L);

        assertThat(result).isEmpty();
        verify(listReservationsPort).findByAccommodationId(1L);
    }

    @Test
    @DisplayName("Should return empty list when listing by admin with no reservations")
    void shouldReturnEmptyListWhenListingByAdminWithNoReservations() {
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(listReservationsPort.findByAdminId(1L)).thenReturn(List.of());

        List<Reservation> result = reservationService.listByAdmin(1L);

        assertThat(result).isEmpty();
        verify(listReservationsPort).findByAdminId(1L);
    }

    @Test
    @DisplayName("Should return empty list when listing by date range with no reservations")
    void shouldReturnEmptyListWhenListingByDateRangeWithNoReservations() {
        LocalDate start = LocalDate.of(2025, 12, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        when(listReservationsPort.findByDateRange(start, end)).thenReturn(List.of());

        List<Reservation> result = reservationService.listByDateRange(start, end);

        assertThat(result).isEmpty();
        verify(listReservationsPort).findByDateRange(start, end);
    }

    @Test
    @DisplayName("Should allow adjacent reservations without overlap")
    void shouldAllowAdjacentReservationsWithoutOverlap() {
        DateRange adjacentRange = new DateRange(
                LocalDate.of(2025, 12, 5),
                LocalDate.of(2025, 12, 10)
        );

        CreateReservationCommand command = new CreateReservationCommand(
                1L,
                1L,
                adjacentRange,
                2,
                "Adjacent reservation"
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, adjacentRange, null))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(testReservation);

        assertThatCode(() -> reservationService.create(command))
                .doesNotThrowAnyException();

        verify(saveReservationPort).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should recalculate price when updating date range")
    void shouldRecalculatePriceWhenUpdatingDateRange() {
        DateRange longerRange = new DateRange(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 10)
        );

        UpdateReservationCommand command = new UpdateReservationCommand(
                1L,
                1L,
                1L,
                longerRange,
                2,
                "Extended stay"
        );

        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, longerRange, 1L))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(testReservation);

        reservationService.update(command);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(saveReservationPort).save(captor.capture());

        Reservation savedReservation = captor.getValue();
        assertThat(savedReservation.totalPrice().value())
                .isEqualByComparingTo(new BigDecimal("900.00"));
        assertThat(savedReservation.nights()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should allow updating to same accommodation and dates")
    void shouldAllowUpdatingToSameAccommodationAndDates() {
        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, 1L))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(testReservation);

        assertThatCode(() -> reservationService.update(testUpdateCommand))
                .doesNotThrowAnyException();

        verify(saveReservationPort).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should exclude own reservation when checking for overlaps during update")
    void shouldExcludeOwnReservationWhenCheckingForOverlapsDuringUpdate() {
        when(loadReservationPort.loadById(1L)).thenReturn(Optional.of(testReservation));
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, 1L))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(testReservation);

        reservationService.update(testUpdateCommand);

        verify(findOverlappingReservationsPort).findOverlapping(1L, testDateRange, 1L);
    }

    @Test
    @DisplayName("Should validate capacity with exact match")
    void shouldValidateCapacityWithExactMatch() {
        CreateReservationCommand command = new CreateReservationCommand(
                1L,
                1L,
                testDateRange,
                4,
                "Full capacity"
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, null))
                .thenReturn(List.of());
        when(saveReservationPort.save(any(Reservation.class))).thenReturn(testReservation);

        assertThatCode(() -> reservationService.create(command))
                .doesNotThrowAnyException();

        verify(saveReservationPort).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should handle multiple overlapping reservations")
    void shouldHandleMultipleOverlappingReservations() {
        Reservation overlap1 = new Reservation(
                2L,
                1L,
                1L,
                new DateRange(LocalDate.of(2025, 12, 2), LocalDate.of(2025, 12, 4)),
                2,
                Money.euros(200.00),
                false,
                LocalDate.now(),
                "Overlap 1"
        );

        Reservation overlap2 = new Reservation(
                3L,
                1L,
                1L,
                new DateRange(LocalDate.of(2025, 12, 3), LocalDate.of(2025, 12, 6)),
                2,
                Money.euros(300.00),
                false,
                LocalDate.now(),
                "Overlap 2"
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findOverlappingReservationsPort.findOverlapping(1L, testDateRange, null))
                .thenReturn(List.of(overlap1, overlap2));

        assertThatThrownBy(() -> reservationService.create(testCreateCommand))
                .isInstanceOf(ReservationOverlapException.class)
                .hasMessageContaining("1");

        verify(saveReservationPort, never()).save(any(Reservation.class));
    }
}