package com.aogdev.rural.application.port.in.admin;

import com.aogdev.rural.domain.model.Admin;

public interface CreateAdminUseCase {
    Admin create(CreateAdminCommand command);
}
