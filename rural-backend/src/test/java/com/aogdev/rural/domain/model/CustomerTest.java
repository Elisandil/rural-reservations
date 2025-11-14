package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Customer Domain Model Tests")
class CustomerTest {

    @Test
    @DisplayName("Should create valid customer")
    void shouldCreateValidCustomer() {
        PersonName name = new PersonName("María", "López García");
        Phone phone = new Phone("+34612345678");
        Email email = new Email("maria@example.com");
        DNI dni = new DNI("12345678Z");

        Customer customer = new Customer(
                1L,
                100L,
                name,
                phone,
                email,
                "España",
                Gender.FEMALE,
                true,
                dni
        );

        assertThat(customer.id()).isEqualTo(1L);
        assertThat(customer.reservationId()).isEqualTo(100L);
        assertThat(customer.name()).isEqualTo(name);
        assertThat(customer.phone()).isEqualTo(phone);
        assertThat(customer.email()).isEqualTo(email);
        assertThat(customer.nationality()).isEqualTo("España");
        assertThat(customer.gender()).isEqualTo(Gender.FEMALE);
        assertThat(customer.isPilgrim()).isTrue();
        assertThat(customer.dni()).isEqualTo(dni);
    }

    @Test
    @DisplayName("Should create customer with optional fields null")
    void shouldCreateCustomerWithOptionalFieldsNull() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Juan", "Pérez"),
                null,
                null,
                "Francia",
                Gender.MALE,
                false,
                new DNI("87654321A")
        );

        assertThat(customer.phone()).isNull();
        assertThat(customer.email()).isNull();
        assertThat(customer.isPilgrim()).isFalse();
    }

    @Test
    @DisplayName("Should default to false when isPilgrim is null")
    void shouldDefaultToFalseWhenIsPilgrimIsNull() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Pedro", "Martínez"),
                new Phone("+34612345678"),
                new Email("pedro@example.com"),
                "Portugal",
                Gender.MALE,
                null,
                new DNI("11111111H")
        );

        assertThat(customer.isPilgrim()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when reservation ID is null")
    void shouldThrowExceptionWhenReservationIdIsNull() {
        assertThatThrownBy(() -> new Customer(
                1L,
                null,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("reservation ID cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when nationality is null")
    void shouldThrowExceptionWhenNationalityIsNull() {
        assertThatThrownBy(() -> new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                null,
                Gender.MALE,
                false,
                new DNI("12345678Z")
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("nationality cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when nationality is blank")
    void shouldThrowExceptionWhenNationalityIsBlank() {
        assertThatThrownBy(() -> new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "   ",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("nationality cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when gender is null")
    void shouldThrowExceptionWhenGenderIsNull() {
        assertThatThrownBy(() -> new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                null,
                false,
                new DNI("12345678Z")
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("gender cannot be null");
    }

    @Test
    @DisplayName("Should return full name correctly")
    void shouldReturnFullNameCorrectly() {
        PersonName name = new PersonName("Ana María", "López García");
        Customer customer = new Customer(
                1L,
                100L,
                name,
                new Phone("+34612345678"),
                new Email("ana@example.com"),
                "España",
                Gender.FEMALE,
                true,
                new DNI("12345678Z")
        );

        assertThat(customer.fullName()).isEqualTo(name.fullName());
    }

    @Test
    @DisplayName("Should handle isPilgrim method correctly when true")
    void shouldHandleIsPilgrimMethodCorrectlyWhenTrue() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Santiago", "Peregrino"),
                new Phone("+34612345678"),
                new Email("santiago@example.com"),
                "España",
                Gender.MALE,
                true,
                new DNI("12345678Z")
        );

        assertThat(customer.isPilgrim()).isTrue();
    }

    @Test
    @DisplayName("Should handle isPilgrim method correctly when false")
    void shouldHandleIsPilgrimMethodCorrectlyWhenFalse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Juan", "Turista"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        );

        assertThat(customer.isPilgrim()).isFalse();
    }

    @Test
    @DisplayName("Should preserve immutability - records are immutable by design")
    void shouldPreserveImmutability() {
        PersonName name1 = new PersonName("Juan", "García");
        Phone phone1 = new Phone("+34612345678");
        Email email1 = new Email("juan@example.com");
        DNI dni1 = new DNI("12345678Z");

        Customer customer1 = new Customer(
                1L,
                100L,
                name1,
                phone1,
                email1,
                "España",
                Gender.MALE,
                false,
                dni1
        );

        PersonName name2 = new PersonName("María", "López");
        Phone phone2 = new Phone("+34687654321");
        Email email2 = new Email("maria@example.com");
        DNI dni2 = new DNI("87654321A");

        Customer customer2 = new Customer(
                customer1.id(),
                customer1.reservationId(),
                name2,
                phone2,
                email2,
                "Francia",
                Gender.FEMALE,
                true,
                dni2
        );

        assertThat(customer2).isNotSameAs(customer1);
        assertThat(customer2.id()).isEqualTo(customer1.id());
        assertThat(customer2.reservationId()).isEqualTo(customer1.reservationId());
        assertThat(customer2.name()).isNotEqualTo(customer1.name());
        assertThat(customer2.phone()).isNotEqualTo(customer1.phone());
        assertThat(customer2.email()).isNotEqualTo(customer1.email());
        assertThat(customer2.nationality()).isNotEqualTo(customer1.nationality());
        assertThat(customer2.gender()).isNotEqualTo(customer1.gender());
        assertThat(customer2.isPilgrim()).isNotEqualTo(customer1.isPilgrim());
        assertThat(customer2.dni()).isNotEqualTo(customer1.dni());
    }

    @Test
    @DisplayName("Should create customer with NIE format")
    void shouldCreateCustomerWithNIEFormat() {
        DNI nie = new DNI("X1234567L");

        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("John", "Smith"),
                new Phone("+34612345678"),
                new Email("john@example.com"),
                "Reino Unido",
                Gender.MALE,
                true,
                nie
        );

        assertThat(customer.dni()).isEqualTo(nie);
        assertThat(customer.dni().value()).isEqualTo("X1234567L");
    }

    @Test
    @DisplayName("Should handle all gender types")
    void shouldHandleAllGenderTypes() {
        Customer male = new Customer(
                1L, 100L, new PersonName("Juan", "García"),
                new Phone("+34612345678"), new Email("juan@example.com"),
                "España", Gender.MALE, false, new DNI("12345678Z")
        );

        Customer female = new Customer(
                2L, 101L, new PersonName("María", "López"),
                new Phone("+34612345678"), new Email("maria@example.com"),
                "España", Gender.FEMALE, false, new DNI("87654321A")
        );

        Customer other = new Customer(
                3L, 102L, new PersonName("Alex", "Pérez"),
                new Phone("+34612345678"), new Email("alex@example.com"),
                "España", Gender.OTHER, false, new DNI("11111111H")
        );

        assertThat(male.gender()).isEqualTo(Gender.MALE);
        assertThat(female.gender()).isEqualTo(Gender.FEMALE);
        assertThat(other.gender()).isEqualTo(Gender.OTHER);
    }

    @Test
    @DisplayName("Should handle long nationality names")
    void shouldHandleLongNationalityNames() {
        String longNationality = "República Democrática del Congo";

        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Jean", "Claude"),
                new Phone("+34612345678"),
                new Email("jean@example.com"),
                longNationality,
                Gender.MALE,
                false,
                new DNI("12345678Z")
        );

        assertThat(customer.nationality()).isEqualTo(longNationality);
    }

    @Test
    @DisplayName("Should validate PersonName constraints through value object")
    void shouldValidatePersonNameConstraintsThroughValueObject() {
        assertThatThrownBy(() -> new Customer(
                1L,
                100L,
                new PersonName("", "García"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("first name cannot be null or blank");
    }

    @Test
    @DisplayName("Should validate Phone constraints through value object")
    void shouldValidatePhoneConstraintsThroughValueObject() {
        assertThatThrownBy(() -> new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("123"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid length");
    }

    @Test
    @DisplayName("Should validate Email constraints through value object")
    void shouldValidateEmailConstraintsThroughValueObject() {
        assertThatThrownBy(() -> new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                new Email("invalid-email"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    @DisplayName("Should validate DNI constraints through value object")
    void shouldValidateDNIConstraintsThroughValueObject() {
        assertThatThrownBy(() -> new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("invalid-dni")
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("invalid format");
    }
}