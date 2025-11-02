package com.aogdev.rural.domain.valueobject;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PersonName Value Object Tests")
class PersonNameTest {

    @Test
    @DisplayName("Should create valid PersonName")
    void shouldCreateValidPersonName() {
        PersonName name = new PersonName("Juan", "García López");

        assertThat(name.firstName()).isEqualTo("Juan");
        assertThat(name.surnames()).isEqualTo("García López");
    }

    @Test
    @DisplayName("Should generate full name correctly with two surnames")
    void shouldGenerateFullNameWithTwoSurnames() {
        PersonName name = new PersonName("Juan Carlos", "García López");

        assertThat(name.fullName()).isEqualTo("Juan Carlos García López");
    }

    @Test
    @DisplayName("Should generate full name correctly with one surname")
    void shouldGenerateFullNameWithOneSurname() {
        PersonName name = new PersonName("Juan", "García");

        assertThat(name.fullName()).isEqualTo("Juan García");
    }

    @Test
    @DisplayName("Should handle multiple first names")
    void shouldHandleMultipleFirstNames() {
        PersonName name = new PersonName("María del Carmen", "Rodríguez Pérez");

        assertThat(name.fullName()).isEqualTo("María del Carmen Rodríguez Pérez");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Should throw exception when first name is blank")
    void shouldThrowExceptionWhenFirstNameIsBlank(String firstName) {
        assertThatThrownBy(() -> new PersonName(firstName, "García"))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("first name cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when first name is null")
    void shouldThrowExceptionWhenFirstNameIsNull() {
        assertThatThrownBy(() -> new PersonName(null, "García"))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("first name cannot be null or blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Should throw exception when surnames are blank")
    void shouldThrowExceptionWhenSurnamesAreBlank(String surnames) {
        assertThatThrownBy(() -> new PersonName("Juan", surnames))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("surnames cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when surnames are null")
    void shouldThrowExceptionWhenSurnamesAreNull() {
        assertThatThrownBy(() -> new PersonName("Juan", null))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("surnames cannot be null or blank");
    }
}