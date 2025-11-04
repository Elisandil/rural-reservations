package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AdminJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AdminJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminPersistenceAdapter Tests")
class AdminPersistenceAdapterTest {

    @Mock
    private AdminJpaRepository repository;

    @InjectMocks
    private AdminPersistenceAdapter adapter;

    private Admin testAdmin;
    private AdminJpaEntity testEntity;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testAdmin = new Admin(
                1L,
                new PersonName("Juan", "García López"),
                new Email("juan.garcia@example.com"),
                new Phone("+34612345678"),
                "hashedPassword123",
                true,
                now,
                now
        );

        testEntity = AdminJpaEntity.builder()
                .id(1L)
                .firstName("Juan")
                .surnames("García López")
                .email("juan.garcia@example.com")
                .phone("+34612345678")
                .passwordHash("hashedPassword123")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    @DisplayName("Should save admin successfully")
    void shouldSaveAdminSuccessfully() {
        when(repository.save(any(AdminJpaEntity.class))).thenReturn(testEntity);

        Admin result = adapter.save(testAdmin);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email().value()).isEqualTo("juan.garcia@example.com");

        verify(repository).save(any(AdminJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle save error gracefully")
    void shouldHandleSaveErrorGracefully() {
        when(repository.save(any(AdminJpaEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> adapter.save(testAdmin))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to save admin");

        verify(repository).save(any(AdminJpaEntity.class));
    }

    @Test
    @DisplayName("Should load admin by id successfully")
    void shouldLoadAdminByIdSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));

        Optional<Admin> result = adapter.loadById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().email().value()).isEqualTo("juan.garcia@example.com");

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when admin not found by id")
    void shouldReturnEmptyWhenAdminNotFoundById() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Admin> result = adapter.loadById(999L);

        assertThat(result).isEmpty();
        verify(repository).findById(999L);
    }

    @Test
    @DisplayName("Should find admin by email successfully")
    void shouldFindAdminByEmailSuccessfully() {
        Email email = new Email("juan.garcia@example.com");
        when(repository.findByEmail("juan.garcia@example.com")).thenReturn(Optional.of(testEntity));

        Optional<Admin> result = adapter.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo(email);

        verify(repository).findByEmail("juan.garcia@example.com");
    }

    @Test
    @DisplayName("Should return empty when admin not found by email")
    void shouldReturnEmptyWhenAdminNotFoundByEmail() {
        Email email = new Email("notfound@example.com");
        when(repository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<Admin> result = adapter.findByEmail(email);

        assertThat(result).isEmpty();
        verify(repository).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should find all admins successfully")
    void shouldFindAllAdminsSuccessfully() {
        AdminJpaEntity entity2 = AdminJpaEntity.builder()
                .id(2L)
                .firstName("María")
                .surnames("Pérez")
                .email("maria@example.com")
                .phone("+34698765432")
                .passwordHash("hash")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findAll()).thenReturn(List.of(testEntity, entity2));

        List<Admin> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should find all active admins successfully")
    void shouldFindAllActiveAdminsSuccessfully() {
        when(repository.findAllByActiveTrue()).thenReturn(List.of(testEntity));

        List<Admin> result = adapter.findAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().isActive()).isTrue();

        verify(repository).findAllByActiveTrue();
    }

    @Test
    @DisplayName("Should return empty list when no admins exist")
    void shouldReturnEmptyListWhenNoAdminsExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<Admin> result = adapter.findAll();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }
}
