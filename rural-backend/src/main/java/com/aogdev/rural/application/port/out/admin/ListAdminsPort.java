package com.aogdev.rural.application.port.out.admin;

import com.aogdev.rural.domain.model.Admin;

import java.util.List;

public interface ListAdminsPort {
    List<Admin> findAll();
    List<Admin> findAllActive();
}
