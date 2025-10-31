package com.aogdev.rural.application.port.in;

import com.aogdev.rural.application.command.CreateAdminCommand;
import com.aogdev.rural.application.command.UpdateAdminCommand;
import com.aogdev.rural.domain.model.Admin;

public interface ManageAdminUseCase {
    Admin create(CreateAdminCommand command);
    Admin update(UpdateAdminCommand command);
    void activate(Long adminId);
    void deactivate(Long adminId);
    void changePassword(Long adminId, String newPassword);
}
