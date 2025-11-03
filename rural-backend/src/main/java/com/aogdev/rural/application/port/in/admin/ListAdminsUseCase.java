package com.aogdev.rural.application.port.in.admin;

import com.aogdev.rural.domain.model.Admin;

import java.util.List;

public interface ListAdminsUseCase {
    List<Admin> listAll();
    List<Admin> listActive();
}
