package com.aogdev.rural.domain.valueobject;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Email Value Object Tests")
class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_123@test-domain.org"
    })
    @DisplayName("Should create valid email")
    void shouldCreateValidEmail(String emailValue) {
        Email email = new Email(emailValue);

        assertThat(email.value()).isEqualTo(emailValue.toLowerCase());
    }

    @Test
    @DisplayName("Should normalize email to lowercase")
    void shouldNormalizeEmailToLowercase() {
        Email email = new Email("Test@EXAMPLE.COM");

        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should trim whitespace")
    void shouldTrimWhitespace() {
        Email email = new Email("  test@example.com  ");

        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "@example.com",
            "user@",
            "user@.com",
            "user space@example.com",
            "user@example"
    })
    @DisplayName("Should throw exception for invalid email format")
    void shouldThrowExceptionForInvalidFormat(String invalidEmail) {
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("cannot be null or blank");
    }

    @Test
    @DisplayName("ToString should return email value")
    void toStringShouldReturnEmailValue() {
        Email email = new Email("test@example.com");

        assertThat(email.toString()).isEqualTo("test@example.com");
    }
}
