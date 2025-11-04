package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.application.port.in.admin.CreateAdminCommand;
import com.aogdev.rural.application.port.in.admin.UpdateAdminCommand;
import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.AdminResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.CreateAdminRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.UpdateAdminRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AdminRestMapper Tests")
class AdminRestMapperTest {

    @Test
    @DisplayName("Should map CreateAdminRequest to CreateAdminCommand")
    void shouldMapCreateRequestToCommand() {
        CreateAdminRequest request = new CreateAdminRequest(
                "Juan",
                "García López",
                "juan@example.com",
                "+34612345678",
                "password123"
        );
        CreateAdminCommand command = AdminRestMapper.toCommand(request);

        assertThat(command.name().firstName()).isEqualTo("Juan");
        assertThat(command.name().surnames()).isEqualTo("García López");
        assertThat(command.email().value()).isEqualTo("juan@example.com");
        assertThat(command.phone().value()).isEqualTo("+34612345678");
        assertThat(command.password()).isEqualTo("password123");
    }

    @Test
    @DisplayName("Should map UpdateAdminRequest to UpdateAdminCommand")
    void shouldMapUpdateRequestToCommand() {
        UpdateAdminRequest request = new UpdateAdminRequest(
                "Juan",
                "García Martínez",
                "juan@example.com",
                "+34612345678"
        );
        UpdateAdminCommand command = AdminRestMapper.toCommand(1L, request);

        assertThat(command.id()).isEqualTo(1L);
        assertThat(command.name().firstName()).isEqualTo("Juan");
        assertThat(command.name().surnames()).isEqualTo("García Martínez");
        assertThat(command.email().value()).isEqualTo("juan@example.com");
        assertThat(command.phone().value()).isEqualTo("+34612345678");
    }

    @Test
    @DisplayName("Should map Admin to AdminResponse")
    void shouldMapAdminToResponse() {
        LocalDateTime now = LocalDateTime.now();
        Admin admin = new Admin(
                1L,
                new PersonName("Juan", "García López"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                "hashedPassword",
                true,
                now,
                now
        );
        AdminResponse response = AdminRestMapper.toResponse(admin);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.firstName()).isEqualTo("Juan");
        assertThat(response.surnames()).isEqualTo("García López");
        assertThat(response.fullName()).isEqualTo("Juan García López");
        assertThat(response.email()).isEqualTo("juan@example.com");
        assertThat(response.phone()).isEqualTo("+34612345678");
        assertThat(response.phoneFormatted()).isEqualTo("+34 612 345 678");
        assertThat(response.active()).isTrue();
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.updatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should map list of Admins to list of AdminResponses")
    void shouldMapAdminListToResponseList() {
        LocalDateTime now = LocalDateTime.now();

        Admin admin1 = new Admin(
                1L,
                new PersonName("Juan", "García"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                "hash",
                true,
                now,
                now
        );

        Admin admin2 = new Admin(
                2L,
                new PersonName("María", "Pérez"),
                new Email("maria@example.com"),
                new Phone("+34698765432"),
                "hash",
                true,
                now,
                now
        );

        List<AdminResponse> responses = AdminRestMapper.toResponseList(List.of(admin1, admin2));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(1).id()).isEqualTo(2L);
    }
}
