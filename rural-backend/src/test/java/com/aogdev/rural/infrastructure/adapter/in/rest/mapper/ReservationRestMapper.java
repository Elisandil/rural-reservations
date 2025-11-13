package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.application.port.in.reservation.CreateReservationCommand;
import com.aogdev.rural.application.port.in.reservation.UpdateReservationCommand;
import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.DateRange;
import com.aogdev.rural.domain.valueobject.Money;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.CreateReservationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.ReservationResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.UpdateReservationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ReservationRestMapper Tests")
class ReservationRestMapperTest {

    @Test
    @DisplayName("Should map CreateReservationRequest to CreateReservationCommand")
    void shouldMapCreateRequestToCommand() {
        CreateReservationRequest request = new CreateReservationRequest(
                1L,
                2L,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5),
                2,
                "Test reservation"
        );

        CreateReservationCommand command = ReservationRestMapper.toCommand(request);

        assertThat(command.accommodationId()).isEqualTo(1L);
        assertThat(command.adminId()).isEqualTo(2L);
        assertThat(command.dateRange().startDate()).isEqualTo(LocalDate.of(2025, 12, 1));
        assertThat(command.dateRange().endDate()).isEqualTo(LocalDate.of(2025, 12, 5));
        assertThat(command.bedsReserved()).isEqualTo(2);
        assertThat(command.notes()).isEqualTo("Test reservation");
    }

    @Test
    @DisplayName("Should map CreateReservationRequest with null notes to command")
    void shouldMapCreateRequestWithNullNotesToCommand() {
        CreateReservationRequest request = new CreateReservationRequest(
                1L,
                2L,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5),
                2,
                null
        );

        CreateReservationCommand command = ReservationRestMapper.toCommand(request);

        assertThat(command.notes()).isNull();
    }

    @Test
    @DisplayName("Should map UpdateReservationRequest to UpdateReservationCommand")
    void shouldMapUpdateRequestToCommand() {
        UpdateReservationRequest request = new UpdateReservationRequest(
                1L,
                2L,
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 15),
                3,
                "Updated reservation"
        );

        UpdateReservationCommand command = ReservationRestMapper.toCommand(5L, request);

        assertThat(command.id()).isEqualTo(5L);
        assertThat(command.accommodationId()).isEqualTo(1L);
        assertThat(command.adminId()).isEqualTo(2L);
        assertThat(command.dateRange().startDate()).isEqualTo(LocalDate.of(2025, 12, 10));
        assertThat(command.dateRange().endDate()).isEqualTo(LocalDate.of(2025, 12, 15));
        assertThat(command.bedsReserved()).isEqualTo(3);
        assertThat(command.notes()).isEqualTo("Updated reservation");
    }

    @Test
    @DisplayName("Should map UpdateReservationRequest with null notes to command")
    void shouldMapUpdateRequestWithNullNotesToCommand() {
        UpdateReservationRequest request = new UpdateReservationRequest(
                1L,
                2L,
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 15),
                3,
                null
        );

        UpdateReservationCommand command = ReservationRestMapper.toCommand(5L, request);

        assertThat(command.notes()).isNull();
    }

    @Test
    @DisplayName("Should map Reservation to ReservationResponse")
    void shouldMapReservationToResponse() {
        Reservation reservation = new Reservation(
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

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.accommodationId()).isEqualTo(100L);
        assertThat(response.accommodationName()).isEqualTo("Casa del Bosque");
        assertThat(response.adminId()).isEqualTo(200L);
        assertThat(response.adminFullName()).isEqualTo("Juan García");
        assertThat(response.startDate()).isEqualTo(LocalDate.of(2025, 12, 1));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2025, 12, 5));
        assertThat(response.nights()).isEqualTo(4L);
        assertThat(response.bedsReserved()).isEqualTo(2);
        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("400.00"));
        assertThat(response.currency()).isEqualTo("EUR");
        assertThat(response.paid()).isFalse();
        assertThat(response.bookingDate()).isEqualTo(LocalDate.of(2025, 11, 15));
        assertThat(response.notes()).isEqualTo("Test reservation");
    }

    @Test
    @DisplayName("Should map Reservation with null id to response")
    void shouldMapReservationWithNullIdToResponse() {
        Reservation reservation = new Reservation(
                null,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Test"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.id()).isNull();
    }

    @Test
    @DisplayName("Should map Reservation with null notes to response")
    void shouldMapReservationWithNullNotesToResponse() {
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

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.notes()).isNull();
    }

    @Test
    @DisplayName("Should map paid Reservation to response")
    void shouldMapPaidReservationToResponse() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                true,
                LocalDate.now(),
                "Paid reservation"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.paid()).isTrue();
    }

    @Test
    @DisplayName("Should calculate nights correctly in response")
    void shouldCalculateNightsCorrectlyInResponse() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 10)),
                2,
                Money.euros(900.00),
                false,
                LocalDate.now(),
                "Long stay"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.nights()).isEqualTo(9L);
    }

    @Test
    @DisplayName("Should map list of Reservations to list of ReservationResponses")
    void shouldMapReservationListToResponseList() {
        Reservation reservation1 = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Reservation 1"
        );

        Reservation reservation2 = new Reservation(
                2L,
                101L,
                201L,
                new DateRange(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15)),
                3,
                Money.euros(600.00),
                true,
                LocalDate.now(),
                "Reservation 2"
        );

        Reservation reservation3 = new Reservation(
                3L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 20), LocalDate.of(2025, 12, 25)),
                4,
                Money.euros(800.00),
                false,
                LocalDate.now(),
                "Reservation 3"
        );

        Function<Long, String> accommodationResolver = id ->
                id == 100L ? "Casa del Bosque" : "Villa del Mar";
        Function<Long, String> adminResolver = id ->
                id == 200L ? "Juan García" : "María López";

        List<ReservationResponse> responses = ReservationRestMapper.toResponseList(
                List.of(reservation1, reservation2, reservation3),
                accommodationResolver,
                adminResolver
        );

        assertThat(responses).hasSize(3);
        assertThat(responses.getFirst().id()).isEqualTo(1L);
        assertThat(responses.get(0).accommodationName()).isEqualTo("Casa del Bosque");
        assertThat(responses.get(0).adminFullName()).isEqualTo("Juan García");
        assertThat(responses.get(1).id()).isEqualTo(2L);
        assertThat(responses.get(1).accommodationName()).isEqualTo("Villa del Mar");
        assertThat(responses.get(1).adminFullName()).isEqualTo("María López");
        assertThat(responses.get(2).id()).isEqualTo(3L);
        assertThat(responses.get(2).accommodationName()).isEqualTo("Casa del Bosque");
        assertThat(responses.get(2).adminFullName()).isEqualTo("Juan García");
    }

    @Test
    @DisplayName("Should map empty list to empty response list")
    void shouldMapEmptyListToEmptyResponseList() {
        Function<Long, String> accommodationResolver = id -> "Unknown";
        Function<Long, String> adminResolver = id -> "Unknown";

        List<ReservationResponse> responses = ReservationRestMapper.toResponseList(
                List.of(),
                accommodationResolver,
                adminResolver
        );

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Should preserve order when mapping list")
    void shouldPreserveOrderWhenMappingList() {
        Reservation reservation1 = new Reservation(
                3L, 100L, 200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2, Money.euros(400.00), false, LocalDate.now(), "C"
        );

        Reservation reservation2 = new Reservation(
                1L, 100L, 200L,
                new DateRange(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15)),
                2, Money.euros(400.00), false, LocalDate.now(), "A"
        );

        Reservation reservation3 = new Reservation(
                2L, 100L, 200L,
                new DateRange(LocalDate.of(2025, 12, 20), LocalDate.of(2025, 12, 25)),
                2, Money.euros(400.00), false, LocalDate.now(), "B"
        );

        Function<Long, String> resolver = id -> "Test";

        List<ReservationResponse> responses = ReservationRestMapper.toResponseList(
                List.of(reservation1, reservation2, reservation3),
                resolver,
                resolver
        );

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).id()).isEqualTo(3L);
        assertThat(responses.get(1).id()).isEqualTo(1L);
        assertThat(responses.get(2).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should handle special characters in names")
    void shouldHandleSpecialCharactersInNames() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Reserva para «El Olivo»"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa «El Paraíso»",
                "José María García-López"
        );

        assertThat(response.accommodationName()).isEqualTo("Casa «El Paraíso»");
        assertThat(response.adminFullName()).isEqualTo("José María García-López");
        assertThat(response.notes()).isEqualTo("Reserva para «El Olivo»");
    }

    @Test
    @DisplayName("Should handle very high prices")
    void shouldHandleVeryHighPrices() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                10,
                Money.euros(new BigDecimal("9999.99")),
                false,
                LocalDate.now(),
                "Luxury reservation"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Villa Presidencial",
                "Juan García"
        );

        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("9999.99"));
    }

    @Test
    @DisplayName("Should handle minimum price")
    void shouldHandleMinimumPrice() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 2)),
                1,
                Money.euros(new BigDecimal("0.01")),
                false,
                LocalDate.now(),
                "Promotional reservation"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Habitación Básica",
                "Juan García"
        );

        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("0.01"));
    }

    @Test
    @DisplayName("Should handle high bed capacity")
    void shouldHandleHighBedCapacity() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                20,
                Money.euros(800.00),
                false,
                LocalDate.now(),
                "Group reservation"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Albergue Rural",
                "Juan García"
        );

        assertThat(response.bedsReserved()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should handle minimum bed capacity")
    void shouldHandleMinimumBedCapacity() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                1,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Solo traveler"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Estudio Individual",
                "Juan García"
        );

        assertThat(response.bedsReserved()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should map list with mixed paid statuses")
    void shouldMapListWithMixedPaidStatuses() {
        Reservation reservation1 = new Reservation(
                1L, 100L, 200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2, Money.euros(400.00), true, LocalDate.now(), "Paid"
        );

        Reservation reservation2 = new Reservation(
                2L, 100L, 200L,
                new DateRange(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15)),
                2, Money.euros(500.00), false, LocalDate.now(), "Unpaid"
        );

        Reservation reservation3 = new Reservation(
                3L, 100L, 200L,
                new DateRange(LocalDate.of(2025, 12, 20), LocalDate.of(2025, 12, 25)),
                2, Money.euros(600.00), true, LocalDate.now(), "Paid"
        );

        Function<Long, String> resolver = id -> "Test";

        List<ReservationResponse> responses = ReservationRestMapper.toResponseList(
                List.of(reservation1, reservation2, reservation3),
                resolver,
                resolver
        );

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).paid()).isTrue();
        assertThat(responses.get(1).paid()).isFalse();
        assertThat(responses.get(2).paid()).isTrue();
    }

    @Test
    @DisplayName("Should handle single element list")
    void shouldHandleSingleElementList() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "Single reservation"
        );

        Function<Long, String> resolver = id -> "Test";

        List<ReservationResponse> responses = ReservationRestMapper.toResponseList(
                List.of(reservation),
                resolver,
                resolver
        );

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should map all fields correctly in response")
    void shouldMapAllFieldsCorrectlyInResponse() {
        Reservation reservation = new Reservation(
                123L,
                456L,
                789L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 8)),
                5,
                Money.euros(new BigDecimal("750.50")),
                true,
                LocalDate.of(2025, 11, 20),
                "Complete test reservation"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Villa Paradise",
                "María López García"
        );

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(123L);
        assertThat(response.accommodationId()).isEqualTo(456L);
        assertThat(response.accommodationName()).isEqualTo("Villa Paradise");
        assertThat(response.adminId()).isEqualTo(789L);
        assertThat(response.adminFullName()).isEqualTo("María López García");
        assertThat(response.startDate()).isEqualTo(LocalDate.of(2025, 12, 1));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2025, 12, 8));
        assertThat(response.nights()).isEqualTo(7L);
        assertThat(response.bedsReserved()).isEqualTo(5);
        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("750.50"));
        assertThat(response.currency()).isEqualTo("EUR");
        assertThat(response.paid()).isTrue();
        assertThat(response.bookingDate()).isEqualTo(LocalDate.of(2025, 11, 20));
        assertThat(response.notes()).isEqualTo("Complete test reservation");
    }

    @Test
    @DisplayName("Should create DateRange correctly when mapping from request")
    void shouldCreateDateRangeCorrectlyWhenMappingFromRequest() {
        CreateReservationRequest request = new CreateReservationRequest(
                1L,
                2L,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 5),
                2,
                "Test"
        );

        CreateReservationCommand command = ReservationRestMapper.toCommand(request);

        assertThat(command.dateRange()).isNotNull();
        assertThat(command.dateRange().startDate()).isEqualTo(LocalDate.of(2025, 12, 1));
        assertThat(command.dateRange().endDate()).isEqualTo(LocalDate.of(2025, 12, 5));
        assertThat(command.dateRange().nights()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should preserve precision when mapping prices")
    void shouldPreservePrecisionWhenMappingPrices() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(new BigDecimal("399.99")),
                false,
                LocalDate.now(),
                "Test"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("399.99"));
        assertThat(response.totalPrice().scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle update command with different id")
    void shouldHandleUpdateCommandWithDifferentId() {
        UpdateReservationRequest request = new UpdateReservationRequest(
                1L,
                2L,
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 15),
                3,
                "Updated"
        );

        UpdateReservationCommand command = ReservationRestMapper.toCommand(999L, request);

        assertThat(command.id()).isEqualTo(999L);
        assertThat(command.accommodationId()).isEqualTo(1L);
        assertThat(command.adminId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should handle long notes text")
    void shouldHandleLongNotesText() {
        String longNotes = "This is a very long reservation note that contains multiple sentences " +
                "and provides detailed information about the reservation requirements, special " +
                "requests, dietary restrictions, arrival time preferences, and other important " +
                "details that the accommodation provider needs to know.";

        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                longNotes
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.notes()).isEqualTo(longNotes);
        assertThat(response.notes().length()).isGreaterThan(200);
    }

    @Test
    @DisplayName("Should handle dates across month boundaries")
    void shouldHandleDatesAcrossMonthBoundaries() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 11, 28), LocalDate.of(2025, 12, 3)),
                2,
                Money.euros(500.00),
                false,
                LocalDate.now(),
                "Cross-month"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.startDate()).isEqualTo(LocalDate.of(2025, 11, 28));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2025, 12, 3));
        assertThat(response.nights()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should handle dates across year boundaries")
    void shouldHandleDatesAcrossYearBoundaries() {
        Reservation reservation = new Reservation(
                1L,
                100L,
                200L,
                new DateRange(LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 3)),
                2,
                Money.euros(400.00),
                false,
                LocalDate.now(),
                "New Year reservation"
        );

        ReservationResponse response = ReservationRestMapper.toResponse(
                reservation,
                "Casa del Bosque",
                "Juan García"
        );

        assertThat(response.startDate()).isEqualTo(LocalDate.of(2025, 12, 30));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2026, 1, 3));
        assertThat(response.nights()).isEqualTo(4L);
    }

    @Test
    @DisplayName("Should handle resolver returning different names for different ids")
    void shouldHandleResolverReturningDifferentNamesForDifferentIds() {
        Reservation reservation1 = new Reservation(
                1L, 100L, 200L,
                new DateRange(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)),
                2, Money.euros(400.00), false, LocalDate.now(), "Test 1"
        );

        Reservation reservation2 = new Reservation(
                2L, 101L, 201L,
                new DateRange(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15)),
                3, Money.euros(600.00), false, LocalDate.now(), "Test 2"
        );

        Function<Long, String> accommodationResolver = id -> {
            if (id == 100L) return "Casa A";
            if (id == 101L) return "Casa B";
            return "Unknown";
        };

        Function<Long, String> adminResolver = id -> {
            if (id == 200L) return "Admin A";
            if (id == 201L) return "Admin B";
            return "Unknown";
        };

        List<ReservationResponse> responses = ReservationRestMapper.toResponseList(
                List.of(reservation1, reservation2),
                accommodationResolver,
                adminResolver
        );

        assertThat(responses.get(0).accommodationName()).isEqualTo("Casa A");
        assertThat(responses.get(0).adminFullName()).isEqualTo("Admin A");
        assertThat(responses.get(1).accommodationName()).isEqualTo("Casa B");
        assertThat(responses.get(1).adminFullName()).isEqualTo("Admin B");
    }
}