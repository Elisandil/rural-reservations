package com.aogdev.rural.infrastructure.config;

import com.aogdev.rural.application.port.out.admin.*;
import com.aogdev.rural.application.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminConfiguration {

    @Bean
    public AdminService adminService(
            SaveAdminPort saveAdminPort,
            LoadAdminPort loadAdminPort,
            FindAdminByEmailPort findAdminByEmailPort,
            ListAdminsPort listAdminsPort,
            HashPasswordPort hashPasswordPort) {

        return new AdminService(
                saveAdminPort,
                loadAdminPort,
                findAdminByEmailPort,
                listAdminsPort,
                hashPasswordPort
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
