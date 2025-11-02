package com.aogdev.rural.domain.model;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Admin Domain Model Tests")
class AdminTest {

    @Test
    @DisplayName("Should create valid admin")
    void shouldCreateValidAdmin() {
        PersonName name = new PersonName("Juan", "García");
        Email email = new Email("juan@example.com");
        Phone phone = new Phone("+34612345678");
        LocalDateTime now = LocalDateTime.now();

        Admin admin = new Admin(
                1L,
                name,
                email,
                phone,
                "hashedPassword",
                true,
                now,
                now
        );

        assertThat(admin.id()).isEqualTo(1L);
        assertThat(admin.name()).isEqualTo(name);
        assertThat(admin.email()).isEqualTo(email);
        assertThat(admin.phone()).isEqualTo(phone);
        assertThat(admin.passwordHash()).isEqualTo("hashedPassword");
        assertThat(admin.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should default to active when active is null")
    void shouldDefaultToActiveWhenNull() {
        Admin admin = new Admin(
                1L,
                new PersonName("Juan", "García"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                "hashedPassword",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertThat(admin.active()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when password hash is null")
    void shouldThrowExceptionWhenPasswordHashIsNull() {
        assertThatThrownBy(() -> new Admin(
                1L,
                new PersonName("Juan", "García"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("password hash cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when password hash is blank")
    void shouldThrowExceptionWhenPasswordHashIsBlank() {
        assertThatThrownBy(() -> new Admin(
                1L,
                new PersonName("Juan", "García"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                "   ",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        )).isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("password hash cannot be empty");
    }

    @Test
    @DisplayName("Should activate admin")
    void shouldActivateAdmin() {
        Admin admin = new Admin(
                1L,
                new PersonName("Juan", "García"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                "hashedPassword",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Admin activated = admin.activate();

        assertThat(activated.isActive()).isTrue();
        assertThat(admin.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should deactivate admin")
    void shouldDeactivateAdmin() {
        Admin admin = new Admin(
                1L,
                new PersonName("Juan", "García"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                "hashedPassword",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Admin deactivated = admin.deactivate();

        assertThat(deactivated.isActive()).isFalse();
        assertThat(admin.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should preserve immutability when activating")
    void shouldPreserveImmutabilityWhenActivating() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        Admin original = new Admin(
                1L,
                new PersonName("Juan", "García"),
                new Email("juan@example.com"),
                new Phone("+34612345678"),
                "hashedPassword",
                false,
                created,
                updated
        );

        Admin activated = original.activate();

        assertThat(activated).isNotSameAs(original);
        assertThat(activated.id()).isEqualTo(original.id());
        assertThat(activated.name()).isEqualTo(original.name());
        assertThat(activated.email()).isEqualTo(original.email());
        assertThat(activated.phone()).isEqualTo(original.phone());
        assertThat(activated.passwordHash()).isEqualTo(original.passwordHash());
        assertThat(activated.createdAt()).isEqualTo(original.createdAt());
        assertThat(activated.updatedAt()).isEqualTo(original.updatedAt());
    }
}
