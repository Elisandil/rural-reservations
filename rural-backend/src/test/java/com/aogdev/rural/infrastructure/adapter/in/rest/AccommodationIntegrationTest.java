package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.CreateAccommodationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodation.UpdateAccommodationRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.CreateAccommodationTypeRequest;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationJpaRepository;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationTypeJpaRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Accommodation Integration Tests")
class AccommodationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccommodationJpaRepository accommodationRepository;

    @Autowired
    private AccommodationTypeJpaRepository accommodationTypeRepository;

    private Short testAccommodationTypeId;

    @BeforeEach
    void setUp() throws Exception {
        CreateAccommodationTypeRequest typeRequest = new CreateAccommodationTypeRequest(
                "Casa Rural",
                "Alojamiento completo en casa rural tradicional"
        );

        String typeResponse = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        testAccommodationTypeId = objectMapper.readTree(typeResponse).get("id").shortValue();
    }

    @AfterEach
    void tearDown() {
        accommodationRepository.deleteAll();
        accommodationTypeRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create accommodation successfully")
    void shouldCreateAccommodationSuccessfully() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Casa del Bosque",
                new BigDecimal("85.00"),
                4
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.accommodationTypeId")
                        .value(testAccommodationTypeId.intValue()))
                .andExpect(jsonPath("$.accommodationTypeName").value("Casa Rural"))
                .andExpect(jsonPath("$.name").value("Casa del Bosque"))
                .andExpect(jsonPath("$.pricePerNight").value(85.00))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.bedCapacity").value(4))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should return conflict when creating accommodation with duplicate name")
    void shouldReturnConflictWhenCreatingAccommodationWithDuplicateName() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Casa del Bosque",
                new BigDecimal("85.00"),
                4
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Accommodation Already Exists"));
    }

    @Test
    @DisplayName("Should return bad request when creating accommodation with invalid data")
    void shouldReturnBadRequestWhenCreatingAccommodationWithInvalidData() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "",
                new BigDecimal("85.00"),
                4
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should return bad request when creating accommodation with negative price")
    void shouldReturnBadRequestWhenCreatingAccommodationWithNegativePrice() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Casa del Bosque",
                new BigDecimal("-10.00"),
                4
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should return bad request when creating accommodation with zero bed capacity")
    void shouldReturnBadRequestWhenCreatingAccommodationWithZeroBedCapacity() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Casa del Bosque",
                new BigDecimal("85.00"),
                0
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should return not found when creating accommodation with non-existent type")
    void shouldReturnNotFoundWhenCreatingAccommodationWithNonExistentType() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                (short) 999,
                "Casa del Bosque",
                new BigDecimal("85.00"),
                4
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Type Not Found"));
    }

    @Test
    @DisplayName("Should get accommodation by id successfully")
    void shouldGetAccommodationByIdSuccessfully() throws Exception {
        CreateAccommodationRequest createRequest = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Casa del Bosque",
                new BigDecimal("85.00"),
                4
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accommodationId = objectMapper.readTree(createResponse).get("id").longValue();

        mockMvc.perform(get("/api/v1/accommodations/{id}", accommodationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accommodationId))
                .andExpect(jsonPath("$.name").value("Casa del Bosque"))
                .andExpect(jsonPath("$.pricePerNight").value(85.00));
    }

    @Test
    @DisplayName("Should return not found when getting non-existent accommodation")
    void shouldReturnNotFoundWhenGettingNonExistentAccommodation() throws Exception {
        mockMvc.perform(get("/api/v1/accommodations/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Not Found"));
    }

    @Test
    @DisplayName("Should get accommodation by name successfully")
    void shouldGetAccommodationByNameSuccessfully() throws Exception {
        CreateAccommodationRequest createRequest = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Casa del Bosque",
                new BigDecimal("85.00"),
                4
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/accommodations/name/{name}", "Casa del Bosque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa del Bosque"));
    }

    @Test
    @DisplayName("Should return not found when getting accommodation by non-existent name")
    void shouldReturnNotFoundWhenGettingAccommodationByNonExistentName() throws Exception {
        mockMvc.perform(get("/api/v1/accommodations/name/{name}", "Inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should list all accommodations successfully")
    void shouldListAllAccommodationsSuccessfully() throws Exception {
        CreateAccommodationRequest request1 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );
        CreateAccommodationRequest request2 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Villa del Mar", new BigDecimal("120.00"), 6
        );
        CreateAccommodationRequest request3 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Apartamento Centro", new BigDecimal("60.00"), 2
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/accommodations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder("Casa del Bosque", "Villa del Mar", "Apartamento Centro")));
    }

    @Test
    @DisplayName("Should return empty list when no accommodations exist")
    void shouldReturnEmptyListWhenNoAccommodationsExist() throws Exception {
        mockMvc.perform(get("/api/v1/accommodations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should list active accommodations only")
    void shouldListActiveAccommodationsOnly() throws Exception {
        CreateAccommodationRequest request1 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );
        CreateAccommodationRequest request2 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Villa del Mar", new BigDecimal("120.00"), 6
        );

        String response1 = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        Long accommodationId1 = objectMapper.readTree(response1).get("id").longValue();

        mockMvc.perform(patch("/api/v1/accommodations/{id}/deactivate", accommodationId1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/accommodations?activeOnly=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Villa del Mar"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    @DisplayName("Should list accommodations by accommodation type")
    void shouldListAccommodationsByAccommodationType() throws Exception {
        CreateAccommodationTypeRequest typeRequest2 = new CreateAccommodationTypeRequest(
                "Apartamento", "Apartamento urbano"
        );

        String typeResponse2 = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeRequest2)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Short typeId2 = objectMapper.readTree(typeResponse2).get("id").shortValue();

        CreateAccommodationRequest request1 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );
        CreateAccommodationRequest request2 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Villa del Mar", new BigDecimal("120.00"), 6
        );
        CreateAccommodationRequest request3 = new CreateAccommodationRequest(
                typeId2, "Apartamento Centro", new BigDecimal("60.00"), 2
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/accommodations?accommodationTypeId=" +
                        testAccommodationTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder("Casa del Bosque", "Villa del Mar")));
    }

    @Test
    @DisplayName("Should return not found when listing by non-existent accommodation type")
    void shouldReturnNotFoundWhenListingByNonExistentAccommodationType() throws Exception {
        mockMvc.perform(get("/api/v1/accommodations?accommodationTypeId=999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Type Not Found"));
    }

    @Test
    @DisplayName("Should update accommodation successfully")
    void shouldUpdateAccommodationSuccessfully() throws Exception {
        CreateAccommodationRequest createRequest = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accommodationId = objectMapper.readTree(createResponse).get("id").longValue();

        UpdateAccommodationRequest updateRequest = new UpdateAccommodationRequest(
                testAccommodationTypeId,
                "Casa del Bosque Premium",
                new BigDecimal("95.00"),
                6
        );

        mockMvc.perform(put("/api/v1/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accommodationId))
                .andExpect(jsonPath("$.name").value("Casa del Bosque Premium"))
                .andExpect(jsonPath("$.pricePerNight").value(95.00))
                .andExpect(jsonPath("$.bedCapacity").value(6));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent accommodation")
    void shouldReturnNotFoundWhenUpdatingNonExistentAccommodation() throws Exception {
        UpdateAccommodationRequest updateRequest = new UpdateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );

        mockMvc.perform(put("/api/v1/accommodations/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Not Found"));
    }

    @Test
    @DisplayName("Should return forbidden when updating inactive accommodation")
    void shouldReturnForbiddenWhenUpdatingInactiveAccommodation() throws Exception {
        CreateAccommodationRequest createRequest = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accommodationId = objectMapper.readTree(createResponse).get("id").longValue();

        mockMvc.perform(patch("/api/v1/accommodations/{id}/deactivate", accommodationId))
                .andExpect(status().isNoContent());

        UpdateAccommodationRequest updateRequest = new UpdateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("95.00"), 4
        );

        mockMvc.perform(put("/api/v1/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Accommodation Not Active"));
    }

    @Test
    @DisplayName("Should return conflict when updating with duplicate name")
    void shouldReturnConflictWhenUpdatingWithDuplicateName() throws Exception {
        CreateAccommodationRequest request1 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );
        CreateAccommodationRequest request2 = new CreateAccommodationRequest(
                testAccommodationTypeId, "Villa del Mar", new BigDecimal("120.00"), 6
        );

        String response1 = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        Long accommodationId1 = objectMapper.readTree(response1).get("id").longValue();

        UpdateAccommodationRequest updateRequest = new UpdateAccommodationRequest(
                testAccommodationTypeId, "Villa del Mar", new BigDecimal("85.00"), 4
        );

        mockMvc.perform(put("/api/v1/accommodations/{id}", accommodationId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Accommodation Already Exists"));
    }

    @Test
    @DisplayName("Should allow updating with same name")
    void shouldAllowUpdatingWithSameName() throws Exception {
        CreateAccommodationRequest createRequest = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accommodationId = objectMapper.readTree(createResponse).get("id").longValue();

        UpdateAccommodationRequest updateRequest = new UpdateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("95.00"), 6
        );

        mockMvc.perform(put("/api/v1/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa del Bosque"))
                .andExpect(jsonPath("$.pricePerNight").value(95.00));
    }

    @Test
    @DisplayName("Should activate accommodation successfully")
    void shouldActivateAccommodationSuccessfully() throws Exception {
        CreateAccommodationRequest createRequest = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accommodationId = objectMapper.readTree(createResponse).get("id").longValue();

        mockMvc.perform(patch("/api/v1/accommodations/{id}/deactivate", accommodationId))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/api/v1/accommodations/{id}/activate", accommodationId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/accommodations/{id}", accommodationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should deactivate accommodation successfully")
    void shouldDeactivateAccommodationSuccessfully() throws Exception {
        CreateAccommodationRequest createRequest = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accommodationId = objectMapper.readTree(createResponse).get("id").longValue();

        mockMvc.perform(patch("/api/v1/accommodations/{id}/deactivate", accommodationId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/accommodations/{id}", accommodationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("Should return not found when activating non-existent accommodation")
    void shouldReturnNotFoundWhenActivatingNonExistentAccommodation() throws Exception {
        mockMvc.perform(patch("/api/v1/accommodations/{id}/activate", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Not Found"));
    }

    @Test
    @DisplayName("Should return not found when deactivating non-existent accommodation")
    void shouldReturnNotFoundWhenDeactivatingNonExistentAccommodation() throws Exception {
        mockMvc.perform(patch("/api/v1/accommodations/{id}/deactivate", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Not Found"));
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void shouldHandleSpecialCharactersInName() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Casa «El Olivo»",
                new BigDecimal("85.00"),
                4
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Casa «El Olivo»"));
    }

    @Test
    @DisplayName("Should handle very high prices")
    void shouldHandleVeryHighPrices() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Villa Presidencial",
                new BigDecimal("9999.99"),
                10
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pricePerNight").value(9999.99));
    }

    @Test
    @DisplayName("Should handle minimum price")
    void shouldHandleMinimumPrice() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Habitación Básica",
                new BigDecimal("0.01"),
                1
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pricePerNight").value(0.01));
    }

    @Test
    @DisplayName("Should handle high bed capacity")
    void shouldHandleHighBedCapacity() throws Exception {
        CreateAccommodationRequest request = new CreateAccommodationRequest(
                testAccommodationTypeId,
                "Albergue Rural",
                new BigDecimal("200.00"),
                20
        );

        mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bedCapacity").value(20));
    }

    @Test
    @DisplayName("Should return bad request when updating with blank name")
    void shouldReturnBadRequestWhenUpdatingWithBlankName() throws Exception {
        CreateAccommodationRequest createRequest = new CreateAccommodationRequest(
                testAccommodationTypeId, "Casa del Bosque", new BigDecimal("85.00"), 4
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accommodationId = objectMapper.readTree(createResponse).get("id").longValue();

        UpdateAccommodationRequest updateRequest = new UpdateAccommodationRequest(
                testAccommodationTypeId, "", new BigDecimal("85.00"), 4
        );

        mockMvc.perform(put("/api/v1/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }
}