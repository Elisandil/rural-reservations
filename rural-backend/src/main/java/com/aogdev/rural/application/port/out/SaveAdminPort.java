package com.aogdev.rural.application.port.out;

import com.aogdev.rural.domain.model.Admin;

public interface SaveAdminPort {
    Admin save(Admin admin);
    void delete(Long adminId);
}
