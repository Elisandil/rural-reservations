package com.aogdev.rural.application.port.in.admin;

import com.aogdev.rural.domain.model.Admin;

public interface GetAdminUseCase {
    Admin getById(Long id);
}
