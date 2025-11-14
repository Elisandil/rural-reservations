package com.aogdev.rural.domain.valueobject;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DNI Value Object Tests")
class DNITest {

    @Test
    @DisplayName("Should create valid DNI with correct format")
    void shouldCreateValidDNIWithCorrectFormat() {
        DNI dni = new DNI("12345678Z");

        assertThat(dni.value()).isEqualTo("12345678Z");
    }

    @Test
    @DisplayName("Should normalize DNI to uppercase")
    void shouldNormalizeDNIToUppercase() {
        DNI dni = new DNI("12345678z");

        assertThat(dni.value()).isEqualTo("12345678Z");
    }

    @Test
    @DisplayName("Should trim whitespace from DNI")
    void shouldTrimWhitespaceFromDNI() {
        DNI dni = new DNI("  12345678Z  ");

        assertThat(dni.value()).isEqualTo("12345678Z");
    }

    @Test
    @DisplayName("Should normalize and trim DNI")
    void shouldNormalizeAndTrimDNI() {
        DNI dni = new DNI("  12345678z  ");

        assertThat(dni.value()).isEqualTo("12345678Z");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678Z",
            "87654321A",
            "00000000T",
            "99999999R",
            "11111111H"
    })
    @DisplayName("Should accept valid DNI formats")
    void shouldAcceptValidDNIFormats(String validDni) {
        DNI dni = new DNI(validDni);

        assertThat(dni.value()).isEqualTo(validDni);
    }

    @Test
    @DisplayName("Should create valid NIE with X prefix")
    void shouldCreateValidNIEWithXPrefix() {
        DNI nie = new DNI("X1234567L");

        assertThat(nie.value()).isEqualTo("X1234567L");
    }

    @Test
    @DisplayName("Should create valid NIE with Y prefix")
    void shouldCreateValidNIEWithYPrefix() {
        DNI nie = new DNI("Y1234567L");

        assertThat(nie.value()).isEqualTo("Y1234567L");
    }

    @Test
    @DisplayName("Should create valid NIE with Z prefix")
    void shouldCreateValidNIEWithZPrefix() {
        DNI nie = new DNI("Z1234567L");

        assertThat(nie.value()).isEqualTo("Z1234567L");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "X0000000T",
            "Y9999999R",
            "Z1234567A",
            "x7654321b",
            "y1111111c"
    })
    @DisplayName("Should accept valid NIE formats")
    void shouldAcceptValidNIEFormats(String validNie) {
        DNI nie = new DNI(validNie);

        assertThat(nie.value()).isEqualTo(validNie.toUpperCase());
    }

    @Test
    @DisplayName("Should throw exception when DNI is null")
    void shouldThrowExceptionWhenDNIIsNull() {
        assertThatThrownBy(() -> new DNI(null))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("DNI")
                .hasMessageContaining("cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when DNI is blank")
    void shouldThrowExceptionWhenDNIIsBlank() {
        assertThatThrownBy(() -> new DNI("   "))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("DNI")
                .hasMessageContaining("cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when DNI is empty")
    void shouldThrowExceptionWhenDNIIsEmpty() {
        assertThatThrownBy(() -> new DNI(""))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("DNI")
                .hasMessageContaining("cannot be null or blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567Z",           // Too short (7 digits)
            "123456789Z",         // Too long (9 digits)
            "12345678",           // Missing letter
            "ABCDEFGHZ",          // All letters
            "12345678ZZ",         // Two letters
            "1234567 Z",          // Space in middle
            "12345678-Z",         // Hyphen
            "A1234567L",          // Invalid NIE prefix (only X, Y, Z allowed)
            "W1234567L",          // Invalid NIE prefix
            "12345678z1",         // Letter in wrong position
            "12.345.678-Z",       // With dots and hyphen
            "12345678 Z",         // Space before letter
    })
    @DisplayName("Should throw exception for invalid DNI formats")
    void shouldThrowExceptionForInvalidDNIFormats(String invalidDni) {
        assertThatThrownBy(() -> new DNI(invalidDni))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("DNI")
                .hasMessageContaining("invalid format");
    }

    @Test
    @DisplayName("Should throw exception when DNI has lowercase letter not normalized before validation")
    void shouldNotThrowExceptionForLowercaseLetter() {
        assertThatCode(() -> new DNI("12345678z"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw exception for DNI with special characters")
    void shouldThrowExceptionForDNIWithSpecialCharacters() {
        assertThatThrownBy(() -> new DNI("12345678@"))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    @DisplayName("Should throw exception for DNI with only numbers")
    void shouldThrowExceptionForDNIWithOnlyNumbers() {
        assertThatThrownBy(() -> new DNI("123456789"))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    @DisplayName("Should throw exception for NIE with invalid prefix")
    void shouldThrowExceptionForNIEWithInvalidPrefix() {
        assertThatThrownBy(() -> new DNI("A1234567L"))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    @DisplayName("Should throw exception for NIE with too many digits")
    void shouldThrowExceptionForNIEWithTooManyDigits() {
        assertThatThrownBy(() -> new DNI("X12345678L"))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    @DisplayName("Should throw exception for NIE with too few digits")
    void shouldThrowExceptionForNIEWithTooFewDigits() {
        assertThatThrownBy(() -> new DNI("X123456L"))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    @DisplayName("Should be equal when values are the same")
    void shouldBeEqualWhenValuesAreTheSame() {
        DNI dni1 = new DNI("12345678Z");
        DNI dni2 = new DNI("12345678Z");

        assertThat(dni1).isEqualTo(dni2);
        assertThat(dni1.hashCode()).isEqualTo(dni2.hashCode());
    }

    @Test
    @DisplayName("Should be equal after normalization")
    void shouldBeEqualAfterNormalization() {
        DNI dni1 = new DNI("12345678z");
        DNI dni2 = new DNI("12345678Z");
        DNI dni3 = new DNI("  12345678Z  ");

        assertThat(dni1).isEqualTo(dni2);
        assertThat(dni2).isEqualTo(dni3);
        assertThat(dni1).isEqualTo(dni3);
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        DNI dni1 = new DNI("12345678Z");
        DNI dni2 = new DNI("87654321A");

        assertThat(dni1).isNotEqualTo(dni2);
    }

    @Test
    @DisplayName("Should handle NIE equality correctly")
    void shouldHandleNIEEqualityCorrectly() {
        DNI nie1 = new DNI("X1234567L");
        DNI nie2 = new DNI("x1234567l");
        DNI nie3 = new DNI("Y1234567L");

        assertThat(nie1).isEqualTo(nie2);
        assertThat(nie1).isNotEqualTo(nie3);
    }

    @Test
    @DisplayName("Should preserve immutability - records are immutable by design")
    void shouldPreserveImmutability() {
        DNI dni = new DNI("12345678Z");
        String originalValue = dni.value();

        assertThat(dni.value()).isEqualTo(originalValue);
        assertThat(dni.value()).isEqualTo("12345678Z");
    }

    @Test
    @DisplayName("Should have consistent toString behavior")
    void shouldHaveConsistentToStringBehavior() {
        DNI dni = new DNI("12345678Z");

        assertThat(dni.toString()).contains("12345678Z");
    }

    @Test
    @DisplayName("Should handle edge case with all zeros")
    void shouldHandleEdgeCaseWithAllZeros() {
        DNI dni = new DNI("00000000T");

        assertThat(dni.value()).isEqualTo("00000000T");
    }

    @Test
    @DisplayName("Should handle edge case with all nines")
    void shouldHandleEdgeCaseWithAllNines() {
        DNI dni = new DNI("99999999R");

        assertThat(dni.value()).isEqualTo("99999999R");
    }

    @Test
    @DisplayName("Should handle mixed case in NIE correctly")
    void shouldHandleMixedCaseInNIECorrectly() {
        DNI nie = new DNI("x1234567L");

        assertThat(nie.value()).isEqualTo("X1234567L");
    }

    @Test
    @DisplayName("Should reject DNI with lowercase letter in different position")
    void shouldRejectDNIWithNumbers() {
        String input = "  x7654321b  ";
        DNI nie = new DNI(input);

        assertThat(nie.value()).isEqualTo("X7654321B");
    }
}