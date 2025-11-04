package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.ChangePasswordRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.CreateAdminRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.UpdateAdminRequest;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AdminJpaRepository;
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
@DisplayName("Admin Integration Tests")
class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminJpaRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should create admin successfully")
    void shouldCreateAdminSuccessfully() throws Exception {
        CreateAdminRequest request = new CreateAdminRequest(
                "Juan",
                "García López",
                "juan@example.com",
                "+34612345678",
                "password123"
        );

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.surnames").value("García López"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.phone").value("+34612345678"))
                .andExpect(jsonPath("$.phoneFormatted").value("+34 612 345 678"))
                .andExpect(jsonPath("$.fullName").value("Juan García López"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("Should return conflict when creating admin with duplicate email")
    void shouldReturnConflictWhenCreatingAdminWithDuplicateEmail() throws Exception {
        CreateAdminRequest request = new CreateAdminRequest(
                "Juan",
                "García López",
                "juan@example.com",
                "+34612345678",
                "password123"
        );

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Admin Already Exists"));
    }

    @Test
    @DisplayName("Should return bad request when creating admin with invalid data")
    void shouldReturnBadRequestWhenCreatingAdminWithInvalidData() throws Exception {
        CreateAdminRequest request = new CreateAdminRequest(
                "",
                "García",
                "invalid-email",
                "123",
                "short"
        );

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    @DisplayName("Should get admin by id successfully")
    void shouldGetAdminByIdSuccessfully() throws Exception {
        CreateAdminRequest createRequest = new CreateAdminRequest(
                "Juan",
                "García López",
                "juan@example.com",
                "+34612345678",
                "password123"
        );

        String createResponse = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long adminId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1/admins/{id}", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminId))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    @DisplayName("Should return not found when getting non-existent admin")
    void shouldReturnNotFoundWhenGettingNonExistentAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admins/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Admin Not Found"));
    }

    @Test
    @DisplayName("Should get admin by email successfully")
    void shouldGetAdminByEmailSuccessfully() throws Exception {
        CreateAdminRequest createRequest = new CreateAdminRequest(
                "Juan",
                "García López",
                "juan@example.com",
                "+34612345678",
                "password123"
        );

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/admins/email/{email}", "juan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    @DisplayName("Should return not found when getting admin by non-existent email")
    void shouldReturnNotFoundWhenGettingAdminByNonExistentEmail() throws Exception {
        mockMvc.perform(get("/api/v1/admins/email/{email}", "notfound@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should list all admins successfully")
    void shouldListAllAdminsSuccessfully() throws Exception {
        CreateAdminRequest request1 = new CreateAdminRequest(
                "Juan", "García", "juan@example.com",
                "+34612345678", "password123"
        );
        CreateAdminRequest request2 = new CreateAdminRequest(
                "María", "Pérez", "maria@example.com",
                "+34698765432", "password123"
        );

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email",
                        containsInAnyOrder("juan@example.com", "maria@example.com")));
    }

    @Test
    @DisplayName("Should update admin successfully")
    void shouldUpdateAdminSuccessfully() throws Exception {
        CreateAdminRequest createRequest = new CreateAdminRequest(
                "Juan", "García López", "juan@example.com",
                "+34612345678", "password123"
        );

        String createResponse = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long adminId = objectMapper.readTree(createResponse).get("id").asLong();

        UpdateAdminRequest updateRequest = new UpdateAdminRequest(
                "Juan Carlos",
                "García Martínez",
                "juan@example.com",
                "+34687654321"
        );

        mockMvc.perform(put("/api/v1/admins/{id}", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminId))
                .andExpect(jsonPath("$.firstName").value("Juan Carlos"))
                .andExpect(jsonPath("$.surnames").value("García Martínez"))
                .andExpect(jsonPath("$.phone").value("+34687654321"));
    }

    @Test
    @DisplayName("Should activate admin successfully")
    void shouldActivateAdminSuccessfully() throws Exception {
        CreateAdminRequest createRequest = new CreateAdminRequest(
                "Juan", "García", "juan@example.com", "+34612345678", "password123"
        );

        String createResponse = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long adminId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(patch("/api/v1/admins/{id}/deactivate", adminId))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/api/v1/admins/{id}/activate", adminId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admins/{id}", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should deactivate admin successfully")
    void shouldDeactivateAdminSuccessfully() throws Exception {
        CreateAdminRequest createRequest = new CreateAdminRequest(
                "Juan", "García", "juan@example.com", "+34612345678", "password123"
        );

        String createResponse = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long adminId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(patch("/api/v1/admins/{id}/deactivate", adminId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admins/{id}", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("Should change password successfully")
    void shouldChangePasswordSuccessfully() throws Exception {
        CreateAdminRequest createRequest = new CreateAdminRequest(
                "Juan", "García", "juan@example.com",
                "+34612345678", "password123"
        );

        String createResponse = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long adminId = objectMapper.readTree(createResponse).get("id").asLong();

        ChangePasswordRequest passwordRequest = new ChangePasswordRequest("newPassword123");

        mockMvc.perform(patch("/api/v1/admins/{id}/password", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return bad request when password is too short")
    void shouldReturnBadRequestWhenPasswordTooShort() throws Exception {
        CreateAdminRequest createRequest = new CreateAdminRequest(
                "Juan", "García", "juan@example.com", "+34612345678", "password123"
        );

        String createResponse = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long adminId = objectMapper.readTree(createResponse).get("id").asLong();

        ChangePasswordRequest passwordRequest = new ChangePasswordRequest("short");

        mockMvc.perform(patch("/api/v1/admins/{id}/password", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should list only active admins when activeOnly is true")
    void shouldListOnlyActiveAdminsWhenActiveOnlyIsTrue() throws Exception {
        CreateAdminRequest request1 = new CreateAdminRequest(
                "Juan", "García", "juan@example.com",
                "+34612345678", "password123"
        );
        CreateAdminRequest request2 = new CreateAdminRequest(
                "María", "Pérez", "maria@example.com",
                "+34698765432", "password123"
        );

        String response1 = mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        Long adminId1 = objectMapper.readTree(response1).get("id").asLong();

        mockMvc.perform(patch("/api/v1/admins/{id}/deactivate", adminId1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admins?activeOnly=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("maria@example.com"));
    }
}
