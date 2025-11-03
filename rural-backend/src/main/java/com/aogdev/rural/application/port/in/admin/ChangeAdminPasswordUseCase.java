package com.aogdev.rural.application.port.in.admin;

public interface ChangeAdminPasswordUseCase {
    void changePassword(Long id, String newPassword);
}
