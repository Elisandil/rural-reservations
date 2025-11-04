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

import java.util.List;
import java.util.stream.Collectors;

public class AdminRestMapper {

    public static CreateAdminCommand toCommand(CreateAdminRequest request) {

        return new CreateAdminCommand(
                new PersonName(request.firstName(), request.surnames()),
                new Email(request.email()),
                new Phone(request.phone()),
                request.password()
        );
    }

    public static UpdateAdminCommand toCommand(Long id,
                                               UpdateAdminRequest request) {

        return new UpdateAdminCommand(
                id,
                new PersonName(request.firstName(), request.surnames()),
                new Email(request.email()),
                new Phone(request.phone())
        );
    }

    public static AdminResponse toResponse(Admin admin) {

        return new AdminResponse(
                admin.id(),
                admin.name().firstName(),
                admin.name().surnames(),
                admin.name().fullName(),
                admin.email().value(),
                admin.phone().value(),
                admin.phone().formatted(),
                admin.active(),
                admin.createdAt(),
                admin.updatedAt()
        );
    }

    public static List<AdminResponse> toResponseList(List<Admin> admins) {

        return admins.stream()
                .map(AdminRestMapper::toResponse)
                .collect(Collectors.toList());
    }
}
