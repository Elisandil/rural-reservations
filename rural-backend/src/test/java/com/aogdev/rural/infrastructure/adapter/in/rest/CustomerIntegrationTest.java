package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.CreateCustomerRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.UpdateCustomerRequest;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.CustomerJpaRepository;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.ReservationJpaRepository;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.ReservationJpaEntity;
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
@DisplayName("Customer Integration Tests")
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private ReservationJpaRepository reservationRepository;

    private Long testReservationId;

    @BeforeEach
    void setUp() {
        ReservationJpaEntity reservation = ReservationJpaEntity.builder()
                .accommodationId(1L)
                .adminId(1L)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .bedsReserved(2)
                .totalPrice(new BigDecimal("200.00"))
                .currency("EUR")
                .paid(false)
                .bookingDate(LocalDate.now())
                .notes("Test reservation")
                .build();

        testReservationId = reservationRepository.save(reservation).getId();
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        reservationRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId,
                "Juan",
                "García López",
                "+34612345678",
                "juan@example.com",
                "España",
                "M",
                false,
                "12345678Z"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.reservationId").value(testReservationId))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.surnames").value("García López"))
                .andExpect(jsonPath("$.fullName").value("Juan García López"))
                .andExpect(jsonPath("$.phone").value("+34612345678"))
                .andExpect(jsonPath("$.phoneFormatted").value("+34 612 345 678"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.nationality").value("España"))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.isPilgrim").value(false))
                .andExpect(jsonPath("$.dni").value("12345678Z"));
    }

    @Test
    @DisplayName("Should create pilgrim customer successfully")
    void shouldCreatePilgrimCustomerSuccessfully() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId,
                "Santiago",
                "Peregrino",
                "+34612345678",
                "santiago@example.com",
                "España",
                "M",
                true,
                "11111111H"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isPilgrim").value(true));
    }

    @Test
    @DisplayName("Should create customer with NIE successfully")
    void shouldCreateCustomerWithNIESuccessfully() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId,
                "John",
                "Smith",
                "+34612345678",
                "john@example.com",
                "Reino Unido",
                "M",
                false,
                "X1234567L"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("X1234567L"))
                .andExpect(jsonPath("$.nationality").value("Reino Unido"));
    }

    @Test
    @DisplayName("Should create customer with null email successfully")
    void shouldCreateCustomerWithNullEmailSuccessfully() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId,
                "María",
                "López",
                "+34612345678",
                null,
                "España",
                "F",
                false,
                "87654321A"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    @DisplayName("Should return conflict when creating customer with duplicate DNI")
    void shouldReturnConflictWhenCreatingCustomerWithDuplicateDNI() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId,
                "Juan",
                "García",
                "+34612345678",
                "juan@example.com",
                "España",
                "M",
                false,
                "12345678Z"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Customer Already Exists"));
    }

    @Test
    @DisplayName("Should return not found when creating customer with non-existent reservation")
    void shouldReturnNotFoundWhenCreatingCustomerWithNonExistentReservation() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                999L,
                "Juan",
                "García",
                "+34612345678",
                "juan@example.com",
                "España",
                "M",
                false,
                "12345678Z"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Reservation Not Found"));
    }

    @Test
    @DisplayName("Should return bad request when creating customer with invalid data")
    void shouldReturnBadRequestWhenCreatingCustomerWithInvalidData() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId,
                "",
                "García",
                "123",
                "invalid-email",
                "",
                "X",
                false,
                "invalid-dni"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() throws Exception {
        CreateCustomerRequest createRequest = new CreateCustomerRequest(
                testReservationId,
                "Juan",
                "García López",
                "+34612345678",
                "juan@example.com",
                "España",
                "M",
                false,
                "12345678Z"
        );

        String createResponse = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.dni").value("12345678Z"));
    }

    @Test
    @DisplayName("Should return not found when getting non-existent customer")
    void shouldReturnNotFoundWhenGettingNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Customer Not Found"));
    }

    @Test
    @DisplayName("Should get customer by DNI successfully")
    void shouldGetCustomerByDNISuccessfully() throws Exception {
        CreateCustomerRequest createRequest = new CreateCustomerRequest(
                testReservationId,
                "Juan",
                "García",
                "+34612345678",
                "juan@example.com",
                "España",
                "M",
                false,
                "12345678Z"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/customers/dni/{dni}", "12345678Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678Z"))
                .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    @DisplayName("Should return not found when getting customer by non-existent DNI")
    void shouldReturnNotFoundWhenGettingCustomerByNonExistentDNI() throws Exception {
        mockMvc.perform(get("/api/v1/customers/dni/{dni}", "99999999R"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should list all customers successfully")
    void shouldListAllCustomersSuccessfully() throws Exception {
        CreateCustomerRequest request1 = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );
        CreateCustomerRequest request2 = new CreateCustomerRequest(
                testReservationId, "María", "López", "+34687654321",
                "maria@example.com", "España", "F", true, "87654321A"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].dni",
                        containsInAnyOrder("12345678Z", "87654321A")));
    }

    @Test
    @DisplayName("Should list customers by reservation id successfully")
    void shouldListCustomersByReservationIdSuccessfully() throws Exception {
        ReservationJpaEntity reservation2 = ReservationJpaEntity.builder()
                .accommodationId(1L).adminId(1L)
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .bedsReserved(2).totalPrice(new BigDecimal("200.00"))
                .currency("EUR").paid(false).bookingDate(LocalDate.now())
                .build();
        Long reservation2Id = reservationRepository.save(reservation2).getId();

        CreateCustomerRequest request1 = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );
        CreateCustomerRequest request2 = new CreateCustomerRequest(
                testReservationId, "María", "López", "+34687654321",
                "maria@example.com", "España", "F", false, "87654321A"
        );
        CreateCustomerRequest request3 = new CreateCustomerRequest(
                reservation2Id, "Pedro", "Martínez", "+34611111111",
                "pedro@example.com", "España", "M", false, "11111111H"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/customers?reservationId=" + testReservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].dni",
                        containsInAnyOrder("12345678Z", "87654321A")));
    }

    @Test
    @DisplayName("Should list only pilgrims when pilgrimsOnly is true")
    void shouldListOnlyPilgrimsWhenPilgrimsOnlyIsTrue() throws Exception {
        CreateCustomerRequest request1 = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );
        CreateCustomerRequest request2 = new CreateCustomerRequest(
                testReservationId, "Santiago", "Peregrino", "+34687654321",
                "santiago@example.com", "España", "M", true, "87654321A"
        );
        CreateCustomerRequest request3 = new CreateCustomerRequest(
                testReservationId, "María", "Caminante", "+34611111111",
                "maria@example.com", "Portugal", "F", true, "11111111H"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/customers?pilgrimsOnly=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].isPilgrim", everyItem(is(true))))
                .andExpect(jsonPath("$[*].dni",
                        containsInAnyOrder("87654321A", "11111111H")));
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() throws Exception {
        CreateCustomerRequest createRequest = new CreateCustomerRequest(
                testReservationId, "Juan", "García López", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );

        String createResponse = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = objectMapper.readTree(createResponse).get("id").asLong();

        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
                testReservationId,
                "Juan Carlos",
                "García Martínez",
                "+34687654321",
                "juancarlos@example.com",
                "Portugal",
                "M",
                true,
                "12345678Z"
        );

        mockMvc.perform(put("/api/v1/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.firstName").value("Juan Carlos"))
                .andExpect(jsonPath("$.surnames").value("García Martínez"))
                .andExpect(jsonPath("$.phone").value("+34687654321"))
                .andExpect(jsonPath("$.email").value("juancarlos@example.com"))
                .andExpect(jsonPath("$.nationality").value("Portugal"))
                .andExpect(jsonPath("$.isPilgrim").value(true))
                .andExpect(jsonPath("$.dni").value("12345678Z"));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent customer")
    void shouldReturnNotFoundWhenUpdatingNonExistentCustomer() throws Exception {
        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );

        mockMvc.perform(put("/api/v1/customers/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Customer Not Found"));
    }

    @Test
    @DisplayName("Should return conflict when updating with duplicate DNI")
    void shouldReturnConflictWhenUpdatingWithDuplicateDNI() throws Exception {
        CreateCustomerRequest request1 = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );
        CreateCustomerRequest request2 = new CreateCustomerRequest(
                testReservationId, "María", "López", "+34687654321",
                "maria@example.com", "España", "F", false, "87654321A"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        String response2 = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customer2Id = objectMapper.readTree(response2).get("id").asLong();

        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
                testReservationId, "María", "López", "+34687654321",
                "maria@example.com", "España", "F", false, "12345678Z"
        );

        mockMvc.perform(put("/api/v1/customers/{id}", customer2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Customer Already Exists"));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() throws Exception {
        CreateCustomerRequest createRequest = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );

        String createResponse = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1/customers/{id}", customerId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent customer")
    void shouldReturnNotFoundWhenDeletingNonExistentCustomer() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Customer Not Found"));
    }

    @Test
    @DisplayName("Should handle all gender types correctly")
    void shouldHandleAllGenderTypesCorrectly() throws Exception {
        CreateCustomerRequest maleRequest = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "11111111H"
        );
        CreateCustomerRequest femaleRequest = new CreateCustomerRequest(
                testReservationId, "María", "López", "+34687654321",
                "maria@example.com", "España", "F", false, "22222222J"
        );
        CreateCustomerRequest otherRequest = new CreateCustomerRequest(
                testReservationId, "Alex", "Pérez", "+34611111111",
                "alex@example.com", "España", "O", false, "33333333P"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gender").value("M"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(femaleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gender").value("F"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otherRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gender").value("O"));
    }

    @Test
    @DisplayName("Should normalize DNI to uppercase")
    void shouldNormalizeDNIToUppercase() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678z"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("12345678Z"));
    }

    @Test
    @DisplayName("Should handle complex names correctly")
    void shouldHandleComplexNamesCorrectly() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId,
                "Ana María Isabel",
                "García López Fernández",
                "+34612345678",
                "ana@example.com",
                "España",
                "F",
                false,
                "12345678Z"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ana María Isabel"))
                .andExpect(jsonPath("$.surnames").value("García López Fernández"))
                .andExpect(jsonPath("$.fullName").value("Ana María Isabel García López"));
    }

    @Test
    @DisplayName("Should handle multiple nationalities correctly")
    void shouldHandleMultipleNationalitiesCorrectly() throws Exception {
        CreateCustomerRequest spanish = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );
        CreateCustomerRequest portuguese = new CreateCustomerRequest(
                testReservationId, "João", "Silva", "+34687654321",
                "joao@example.com", "Portugal", "M", false, "87654321A"
        );
        CreateCustomerRequest british = new CreateCustomerRequest(
                testReservationId, "John", "Smith", "+34611111111",
                "john@example.com", "Reino Unido", "M", false, "X1234567L"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spanish)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nationality").value("España"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portuguese)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nationality").value("Portugal"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(british)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nationality").value("Reino Unido"));
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void shouldReturnEmptyListWhenNoCustomersExist() throws Exception {
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return empty list when no pilgrims exist")
    void shouldReturnEmptyListWhenNoPilgrimsExist() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                testReservationId, "Juan", "García", "+34612345678",
                "juan@example.com", "España", "M", false, "12345678Z"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/customers?pilgrimsOnly=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}