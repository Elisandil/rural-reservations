package com.aogdev.rural.domain.valueobject;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Phone Value Object Tests")
class PhoneTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "+34612345678",
            "612345678",
            "+1234567890",
            "123-456-7890"
    })
    @DisplayName("Should create valid phone")
    void shouldCreateValidPhone(String phoneValue) {
        Phone phone = new Phone(phoneValue);

        assertThat(phone.value()).isNotBlank();
        assertThat(phone.value()).matches("[0-9+]+");
    }

    @Test
    @DisplayName("Should remove non-numeric characters except plus")
    void shouldRemoveNonNumericCharacters() {
        Phone phone = new Phone("+34 612 34 56 78");

        assertThat(phone.value()).isEqualTo("+34612345678");
    }

    @Test
    @DisplayName("Should format Spanish phone correctly")
    void shouldFormatSpanishPhoneCorrectly() {
        Phone phone = new Phone("+34612345678");

        assertThat(phone.formatted()).isEqualTo("+34 612 345 678");
    }

    @Test
    @DisplayName("Should return original value when not Spanish format")
    void shouldReturnOriginalValueWhenNotSpanishFormat() {
        Phone phone = new Phone("+1234567890");

        assertThat(phone.formatted()).isEqualTo("+1234567890");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345",
            "1234567890123456",
            "+1234"
    })
    @DisplayName("Should throw exception for invalid phone length")
    void shouldThrowExceptionForInvalidLength(String invalidPhone) {
        assertThatThrownBy(() -> new Phone(invalidPhone))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid length");
    }

    @Test
    @DisplayName("Should throw exception when phone is null")
    void shouldThrowExceptionWhenPhoneIsNull() {
        assertThatThrownBy(() -> new Phone(null))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when phone is blank")
    void shouldThrowExceptionWhenPhoneIsBlank() {
        assertThatThrownBy(() -> new Phone("   "))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("cannot be null or blank");
    }
}