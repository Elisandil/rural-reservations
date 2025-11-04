package com.aogdev.rural.infrastructure.adapter.out.security;

import com.aogdev.rural.application.port.out.admin.HashPasswordPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DisplayName("BCryptPasswordHashAdapter Tests")
class BCryptPasswordHashAdapterTest {
    private HashPasswordPort hashPasswordPort;

    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        hashPasswordPort = new BCryptPasswordHashAdapter(passwordEncoder);
    }

    @Test
    @DisplayName("Should hash password successfully")
    void shouldHashPasswordSuccessfully() {
        String plainPassword = "password123";

        String hashedPassword = hashPasswordPort.hash(plainPassword);

        assertThat(hashedPassword).isNotNull();
        assertThat(hashedPassword).isNotEqualTo(plainPassword);
        assertThat(hashedPassword).startsWith("$2a$");
    }

    @Test
    @DisplayName("Should generate different hashes for same password")
    void shouldGenerateDifferentHashesForSamePassword() {
        String plainPassword = "password123";

        String hash1 = hashPasswordPort.hash(plainPassword);
        String hash2 = hashPasswordPort.hash(plainPassword);

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Should verify correct password")
    void shouldVerifyCorrectPassword() {
        String plainPassword = "password123";
        String hashedPassword = hashPasswordPort.hash(plainPassword);

        boolean result = hashPasswordPort.verify(plainPassword, hashedPassword);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should reject incorrect password")
    void shouldRejectIncorrectPassword() {
        String plainPassword = "password123";
        String wrongPassword = "wrongPassword";
        String hashedPassword = hashPasswordPort.hash(plainPassword);

        boolean result = hashPasswordPort.verify(wrongPassword, hashedPassword);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle empty password verification")
    void shouldHandleEmptyPasswordVerification() {
        String hashedPassword = hashPasswordPort.hash("password123");

        boolean result = hashPasswordPort.verify("", hashedPassword);

        assertThat(result).isFalse();
    }
}
