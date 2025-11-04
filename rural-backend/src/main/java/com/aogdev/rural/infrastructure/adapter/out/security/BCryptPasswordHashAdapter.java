package com.aogdev.rural.infrastructure.adapter.out.security;

import com.aogdev.rural.application.port.out.admin.HashPasswordPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BCryptPasswordHashAdapter implements HashPasswordPort {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String plainPassword) {
        log.debug("Hashing password");

        try {
            return passwordEncoder.encode(plainPassword);
        } catch (Exception ex) {
            log.error("Error hashing password: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to hash password", ex);
        }
    }

    @Override
    public boolean verify(String plainPassword, String hashedPassword) {
        log.debug("Verifying password");

        try {
            return passwordEncoder.matches(plainPassword, hashedPassword);
        } catch (Exception ex) {
            log.error("Error verifying password: {}", ex.getMessage(), ex);
            return false;
        }
    }
}
