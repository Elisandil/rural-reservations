package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.CreateAccommodationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.CreateAccommodationTypeRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.CreateAdminRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.CreateReservationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.reservation.UpdateReservationRequest;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationJpaRepository;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationTypeJpaRepository;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AdminJpaRepository;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.ReservationJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Reservation Integration Tests")
class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationJpaRepository reservationRepository;

    @Autowired
    private AccommodationJpaRepository accommodationRepository;

    @Autowired
    private AccommodationTypeJpaRepository accommodationTypeRepository;

    @Autowired
    private AdminJpaRepository adminRepository;

    private Long accommodationId;
    private Long adminId;

    @BeforeEach
    void setUp() throws Exception {
        CreateAccommodationTypeRequest typeRequest = new CreateAccommodationTypeRequest(
                "Casa Rural",
                "Alojamiento completo"
        );

        String typeResponse = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Short typeId = objectMapper.readTree(typeResponse).get("id").shortValue();

        CreateAccommodationRequest accommodationRequest = new CreateAccommodationRequest(
                typeId,
                "Casa del Bosque",
                new BigDecimal("100.00"),
                4
        );

        String accommodationResponse = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accommodationRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        accommodationId = objectMapper.readTree(accommodationResponse).get("id").asLong();

        CreateAdminRequest adminRequest = new CreateAdminRequest(
                "Juan",
                "García López",
                "juan@example.com",
                "+34612345678",
                "password123"
        );

        String adminResponse = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        adminId = objectMapper.readTree(adminResponse).get("id").asLong();
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
        accommodationRepository.deleteAll();
        accommodationTypeRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void shouldCreateReservationSuccessfully() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId,
                adminId,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                2,
                "Test reservation"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.accommodationId").value(accommodationId))
                .andExpect(jsonPath("$.accommodationName").value("Casa del Bosque"))
                .andExpect(jsonPath("$.adminId").value(adminId))
                .andExpect(jsonPath("$.adminFullName").value("Juan García López"))
                .andExpect(jsonPath("$.bedsReserved").value(2))
                .andExpect(jsonPath("$.nights").value(5))
                .andExpect(jsonPath("$.totalPrice").value(500.00))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.paid").value(false))
                .andExpect(jsonPath("$.notes").value("Test reservation"))
                .andExpect(jsonPath("$.bookingDate").exists())
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").exists());
    }

    @Test
    @DisplayName("Should return not found when creating reservation with non-existent accommodation")
    void shouldReturnNotFoundWhenCreatingReservationWithNonExistentAccommodation() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                999L,
                adminId,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                2,
                "Test"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Not Found"));
    }

    @Test
    @DisplayName("Should return not found when creating reservation with non-existent admin")
    void shouldReturnNotFoundWhenCreatingReservationWithNonExistentAdmin() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId,
                999L,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                2,
                "Test"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Admin Not Found"));
    }

    @Test
    @DisplayName("Should return bad request when beds exceed capacity")
    void shouldReturnBadRequestWhenBedsExceedCapacity() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId,
                adminId,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                10,
                "Test"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Insufficient Capacity"));
    }

    @Test
    @DisplayName("Should return conflict when dates overlap with existing reservation")
    void shouldReturnConflictWhenDatesOverlapWithExistingReservation() throws Exception {
        CreateReservationRequest request1 = new CreateReservationRequest(
                accommodationId,
                adminId,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                2,
                "First reservation"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        CreateReservationRequest request2 = new CreateReservationRequest(
                accommodationId,
                adminId,
                LocalDate.now().plusDays(12),
                LocalDate.now().plusDays(17),
                2,
                "Overlapping reservation"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Reservation Overlap"));
    }

    @Test
    @DisplayName("Should allow adjacent reservations without overlap")
    void shouldAllowAdjacentReservationsWithoutOverlap() throws Exception {
        CreateReservationRequest request1 = new CreateReservationRequest(
                accommodationId,
                adminId,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                2,
                "First reservation"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        CreateReservationRequest request2 = new CreateReservationRequest(
                accommodationId,
                adminId,
                LocalDate.now().plusDays(15),
                LocalDate.now().plusDays(20),
                2,
                "Adjacent reservation"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return bad request when creating reservation with invalid data")
    void shouldReturnBadRequestWhenCreatingReservationWithInvalidData() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId,
                adminId,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                0,
                "Test"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should get reservation by id successfully")
    void shouldGetReservationByIdSuccessfully() throws Exception {
        CreateReservationRequest createRequest = new CreateReservationRequest(
                accommodationId,
                adminId,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                2,
                "Test reservation"
        );

        String createResponse = mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reservationId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.accommodationId").value(accommodationId))
                .andExpect(jsonPath("$.adminId").value(adminId));
    }

    @Test
    @DisplayName("Should return not found when getting non-existent reservation")
    void shouldReturnNotFoundWhenGettingNonExistentReservation() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Reservation Not Found"));
    }

    @Test
    @DisplayName("Should list all reservations successfully")
    void shouldListAllReservationsSuccessfully() throws Exception {
        CreateReservationRequest request1 = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Reservation 1"
        );

        CreateReservationRequest request2 = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(25),
                3, "Reservation 2"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Should list reservations by accommodation successfully")
    void shouldListReservationsByAccommodationSuccessfully() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Test"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/reservations?accommodationId={id}", accommodationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accommodationId").value(accommodationId));
    }

    @Test
    @DisplayName("Should list reservations by admin successfully")
    void shouldListReservationsByAdminSuccessfully() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Test"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/reservations?adminId={id}", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].adminId").value(adminId));
    }

    @Test
    @DisplayName("Should list reservations by date range successfully")
    void shouldListReservationsByDateRangeSuccessfully() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Test"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/reservations?startDate={start}&endDate={end}",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(30)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Should list unpaid reservations successfully")
    void shouldListUnpaidReservationsSuccessfully() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Test"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/reservations?unpaidOnly=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].paid").value(false));
    }

    @Test
    @DisplayName("Should update reservation successfully")
    void shouldUpdateReservationSuccessfully() throws Exception {
        CreateReservationRequest createRequest = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Original reservation"
        );

        String createResponse = mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reservationId = objectMapper.readTree(createResponse).get("id").asLong();

        UpdateReservationRequest updateRequest = new UpdateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(25),
                3, "Updated reservation"
        );

        mockMvc.perform(put("/api/v1/reservations/{id}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.bedsReserved").value(3))
                .andExpect(jsonPath("$.notes").value("Updated reservation"));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent reservation")
    void shouldReturnNotFoundWhenUpdatingNonExistentReservation() throws Exception {
        UpdateReservationRequest request = new UpdateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Test"
        );

        mockMvc.perform(put("/api/v1/reservations/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Reservation Not Found"));
    }

    @Test
    @DisplayName("Should mark reservation as paid successfully")
    void shouldMarkReservationAsPaidSuccessfully() throws Exception {
        CreateReservationRequest createRequest = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Test"
        );

        String createResponse = mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reservationId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(patch("/api/v1/reservations/{id}/mark-paid", reservationId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid").value(true));
    }

    @Test
    @DisplayName("Should return not found when marking non-existent reservation as paid")
    void shouldReturnNotFoundWhenMarkingNonExistentReservationAsPaid() throws Exception {
        mockMvc.perform(patch("/api/v1/reservations/{id}/mark-paid", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Reservation Not Found"));
    }

    @Test
    @DisplayName("Should cancel reservation successfully")
    void shouldCancelReservationSuccessfully() throws Exception {
        CreateReservationRequest createRequest = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Test"
        );

        String createResponse = mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reservationId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1/reservations/{id}", reservationId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/reservations/{id}", reservationId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return not found when cancelling non-existent reservation")
    void shouldReturnNotFoundWhenCancellingNonExistentReservation() throws Exception {
        mockMvc.perform(delete("/api/v1/reservations/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Reservation Not Found"));
    }

    @Test
    @DisplayName("Should create reservation with null notes")
    void shouldCreateReservationWithNullNotes() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, null
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notes").isEmpty());
    }

    @Test
    @DisplayName("Should filter unpaid reservations correctly")
    void shouldFilterUnpaidReservationsCorrectly() throws Exception {
        CreateReservationRequest request1 = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                2, "Unpaid"
        );

        CreateReservationRequest request2 = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(25),
                2, "To be paid"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        String response2 = mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reservationId2 = objectMapper.readTree(response2).get("id").asLong();

        mockMvc.perform(patch("/api/v1/reservations/{id}/mark-paid", reservationId2))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/reservations?unpaidOnly=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].notes").value("Unpaid"));
    }

    @Test
    @DisplayName("Should calculate total price correctly based on nights")
    void shouldCalculateTotalPriceCorrectlyBasedOnNights() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(17),
                2, "Week stay"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nights").value(7))
                .andExpect(jsonPath("$.totalPrice").value(700.00));
    }

    @Test
    @DisplayName("Should handle reservations with maximum bed capacity")
    void shouldHandleReservationsWithMaximumBedCapacity() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                accommodationId, adminId,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                4, "Full capacity"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bedsReserved").value(4));
    }
}