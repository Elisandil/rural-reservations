package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.CreateAccommodationTypeRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.accommodationType.UpdateAccommodationTypeRequest;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationTypeJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AccommodationType Integration Tests")
class AccommodationTypeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccommodationTypeJpaRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should create accommodation type successfully")
    void shouldCreateAccommodationTypeSuccessfully() throws Exception {
        CreateAccommodationTypeRequest request = new CreateAccommodationTypeRequest(
                "Casa Rural",
                "Alojamiento completo en casa rural tradicional"
        );

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Casa Rural"))
                .andExpect(jsonPath("$.description")
                        .value("Alojamiento completo en casa rural tradicional"));
    }

    @Test
    @DisplayName("Should create accommodation type with null description")
    void shouldCreateAccommodationTypeWithNullDescription() throws Exception {
        CreateAccommodationTypeRequest request = new CreateAccommodationTypeRequest(
                "Apartamento",
                null
        );

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Apartamento"))
                .andExpect(jsonPath("$.description").doesNotExist());
    }

    @Test
    @DisplayName("Should return conflict when creating accommodation type with duplicate name")
    void shouldReturnConflictWhenCreatingAccommodationTypeWithDuplicateName() throws Exception {
        CreateAccommodationTypeRequest request = new CreateAccommodationTypeRequest(
                "Casa Rural",
                "Alojamiento completo"
        );

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Accommodation Type Already Exists"));
    }

    @Test
    @DisplayName("Should return bad request when creating accommodation type with invalid data")
    void shouldReturnBadRequestWhenCreatingAccommodationTypeWithInvalidData() throws Exception {
        CreateAccommodationTypeRequest request = new CreateAccommodationTypeRequest(
                "",
                "Descripción"
        );

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should get accommodation type by id successfully")
    void shouldGetAccommodationTypeByIdSuccessfully() throws Exception {
        CreateAccommodationTypeRequest createRequest = new CreateAccommodationTypeRequest(
                "Casa Rural",
                "Alojamiento completo"
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Short typeId = objectMapper.readTree(createResponse).get("id").shortValue();

        mockMvc.perform(get("/api/v1/accommodation-types/{id}", typeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(typeId.intValue()))
                .andExpect(jsonPath("$.name").value("Casa Rural"));
    }

    @Test
    @DisplayName("Should return not found when getting non-existent accommodation type")
    void shouldReturnNotFoundWhenGettingNonExistentAccommodationType() throws Exception {
        mockMvc.perform(get("/api/v1/accommodation-types/{id}", (short) 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Type Not Found"));
    }

    @Test
    @DisplayName("Should get accommodation type by name successfully")
    void shouldGetAccommodationTypeByNameSuccessfully() throws Exception {
        CreateAccommodationTypeRequest createRequest = new CreateAccommodationTypeRequest(
                "Casa Rural",
                "Alojamiento completo"
        );

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/accommodation-types/name/{name}", "Casa Rural"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa Rural"));
    }

    @Test
    @DisplayName("Should return not found when getting accommodation type by non-existent name")
    void shouldReturnNotFoundWhenGettingAccommodationTypeByNonExistentName() throws Exception {
        mockMvc.perform(get("/api/v1/accommodation-types/name/{name}", "Inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should list all accommodation types successfully")
    void shouldListAllAccommodationTypesSuccessfully() throws Exception {
        CreateAccommodationTypeRequest request1 = new CreateAccommodationTypeRequest(
                "Casa Rural", "Alojamiento completo"
        );
        CreateAccommodationTypeRequest request2 = new CreateAccommodationTypeRequest(
                "Apartamento", "Apartamento turístico"
        );
        CreateAccommodationTypeRequest request3 = new CreateAccommodationTypeRequest(
                "Bungalow", "Bungalow con jardín"
        );

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/accommodation-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder("Casa Rural", "Apartamento", "Bungalow")));
    }

    @Test
    @DisplayName("Should return empty list when no accommodation types exist")
    void shouldReturnEmptyListWhenNoAccommodationTypesExist() throws Exception {
        mockMvc.perform(get("/api/v1/accommodation-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should update accommodation type successfully")
    void shouldUpdateAccommodationTypeSuccessfully() throws Exception {
        CreateAccommodationTypeRequest createRequest = new CreateAccommodationTypeRequest(
                "Casa Rural", "Alojamiento completo"
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Short typeId = objectMapper.readTree(createResponse).get("id").shortValue();

        UpdateAccommodationTypeRequest updateRequest = new UpdateAccommodationTypeRequest(
                "Casa Rural Premium",
                "Alojamiento de lujo en casa rural"
        );

        mockMvc.perform(put("/api/v1/accommodation-types/{id}", typeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(typeId.intValue()))
                .andExpect(jsonPath("$.name").value("Casa Rural Premium"))
                .andExpect(jsonPath("$.description")
                        .value("Alojamiento de lujo en casa rural"));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent accommodation type")
    void shouldReturnNotFoundWhenUpdatingNonExistentAccommodationType() throws Exception {
        UpdateAccommodationTypeRequest updateRequest = new UpdateAccommodationTypeRequest(
                "Casa Rural", "Descripción"
        );

        mockMvc.perform(put("/api/v1/accommodation-types/{id}", (short) 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Type Not Found"));
    }

    @Test
    @DisplayName("Should return conflict when updating with duplicate name")
    void shouldReturnConflictWhenUpdatingWithDuplicateName() throws Exception {
        CreateAccommodationTypeRequest request1 = new CreateAccommodationTypeRequest(
                "Casa Rural", "Primera casa"
        );
        CreateAccommodationTypeRequest request2 = new CreateAccommodationTypeRequest(
                "Apartamento", "Apartamento turístico"
        );

        String response1 = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        Short typeId1 = objectMapper.readTree(response1).get("id").shortValue();

        UpdateAccommodationTypeRequest updateRequest = new UpdateAccommodationTypeRequest(
                "Apartamento", "Nueva descripción"
        );

        mockMvc.perform(put("/api/v1/accommodation-types/{id}", typeId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Accommodation Type Already Exists"));
    }

    @Test
    @DisplayName("Should update accommodation type with null description")
    void shouldUpdateAccommodationTypeWithNullDescription() throws Exception {
        CreateAccommodationTypeRequest createRequest = new CreateAccommodationTypeRequest(
                "Casa Rural", "Con descripción"
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Short typeId = objectMapper.readTree(createResponse).get("id").shortValue();

        UpdateAccommodationTypeRequest updateRequest = new UpdateAccommodationTypeRequest(
                "Casa Rural", null
        );

        mockMvc.perform(put("/api/v1/accommodation-types/{id}", typeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa Rural"))
                .andExpect(jsonPath("$.description").doesNotExist());
    }

    @Test
    @DisplayName("Should delete accommodation type successfully")
    void shouldDeleteAccommodationTypeSuccessfully() throws Exception {
        CreateAccommodationTypeRequest createRequest = new CreateAccommodationTypeRequest(
                "Casa Rural", "Para eliminar"
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Short typeId = objectMapper.readTree(createResponse).get("id").shortValue();

        mockMvc.perform(delete("/api/v1/accommodation-types/{id}", typeId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/accommodation-types/{id}", typeId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent accommodation type")
    void shouldReturnNotFoundWhenDeletingNonExistentAccommodationType() throws Exception {
        mockMvc.perform(delete("/api/v1/accommodation-types/{id}", (short) 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Accommodation Type Not Found"));
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void shouldHandleSpecialCharactersInName() throws Exception {
        CreateAccommodationTypeRequest request = new CreateAccommodationTypeRequest(
                "Casa «El Olivo»",
                "Con carácter especial"
        );

        mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Casa «El Olivo»"));
    }

    @Test
    @DisplayName("Should return bad request when updating with blank name")
    void shouldReturnBadRequestWhenUpdatingWithBlankName() throws Exception {
        CreateAccommodationTypeRequest createRequest = new CreateAccommodationTypeRequest(
                "Casa Rural", "Descripción"
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Short typeId = objectMapper.readTree(createResponse).get("id").shortValue();

        UpdateAccommodationTypeRequest updateRequest = new UpdateAccommodationTypeRequest(
                "", "Nueva descripción"
        );

        mockMvc.perform(put("/api/v1/accommodation-types/{id}", typeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should allow updating with same name")
    void shouldAllowUpdatingWithSameName() throws Exception {
        CreateAccommodationTypeRequest createRequest = new CreateAccommodationTypeRequest(
                "Casa Rural", "Descripción original"
        );

        String createResponse = mockMvc.perform(post("/api/v1/accommodation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Short typeId = objectMapper.readTree(createResponse).get("id").shortValue();

        UpdateAccommodationTypeRequest updateRequest = new UpdateAccommodationTypeRequest(
                "Casa Rural", "Descripción actualizada"
        );

        mockMvc.perform(put("/api/v1/accommodation-types/{id}", typeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa Rural"))
                .andExpect(jsonPath("$.description").value("Descripción actualizada"));
    }
}