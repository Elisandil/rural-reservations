package com.aogdev.rural.infrastructure.adapter.out.security;

import com.aogdev.rural.application.port.out.HashPasswordPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BCryptPasswordAdapter implements HashPasswordPort {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String plainPassword) {
        log.debug("Hashing password");
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public Boolean verify(String plainPassword, String hashedPassword) {
        log.debug("Verifying password");
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}