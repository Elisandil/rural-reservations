package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.admin.CreateAdminCommand;
import com.aogdev.rural.application.port.in.admin.UpdateAdminCommand;
import com.aogdev.rural.application.port.out.admin.*;
import com.aogdev.rural.domain.exception.admin.AdminAlreadyExistsException;
import com.aogdev.rural.domain.exception.admin.AdminNotActiveException;
import com.aogdev.rural.domain.exception.admin.AdminNotFoundException;
import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Unit Tests")
class AdminServiceTest {

    @Mock
    private SaveAdminPort saveAdminPort;

    @Mock
    private LoadAdminPort loadAdminPort;

    @Mock
    private FindAdminByEmailPort findAdminByEmailPort;

    @Mock
    private ListAdminsPort listAdminsPort;

    @Mock
    private HashPasswordPort hashPasswordPort;

    private AdminService adminService;

    private Admin testAdmin;
    private CreateAdminCommand createCommand;
    private UpdateAdminCommand updateCommand;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(
                saveAdminPort,
                loadAdminPort,
                findAdminByEmailPort,
                listAdminsPort,
                hashPasswordPort
        );

        PersonName name = new PersonName("Juan", "García López");
        Email email = new Email("juan.garcia@example.com");
        Phone phone = new Phone("+34612345678");

        testAdmin = new Admin(
                1L,
                name,
                email,
                phone,
                "hashedPassword123",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        createCommand = new CreateAdminCommand(
                name,
                email,
                phone,
                "password123"
        );

        updateCommand = new UpdateAdminCommand(
                1L,
                new PersonName("Juan", "García Martínez"),
                email,
                phone
        );
    }

    @Test
    @DisplayName("Should create admin successfully when email is unique")
    void shouldCreateAdminSuccessfully() {
        when(findAdminByEmailPort.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        when(hashPasswordPort.hash(anyString())).thenReturn("hashedPassword123");
        when(saveAdminPort.save(any(Admin.class))).thenReturn(testAdmin);

        Admin result = adminService.create(createCommand);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(createCommand.email());
        assertThat(result.name()).isEqualTo(createCommand.name());
        assertThat(result.isActive()).isTrue();

        verify(findAdminByEmailPort).findByEmail(createCommand.email());
        verify(hashPasswordPort).hash(createCommand.password());
        verify(saveAdminPort).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should throw AdminAlreadyExistsException when email is duplicate")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(findAdminByEmailPort.findByEmail(any(Email.class))).thenReturn(Optional.of(testAdmin));

        assertThatThrownBy(() -> adminService.create(createCommand))
                .isInstanceOf(AdminAlreadyExistsException.class)
                .hasMessageContaining(createCommand.email().value());

        verify(findAdminByEmailPort).findByEmail(createCommand.email());
        verify(saveAdminPort, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should hash password when creating admin")
    void shouldHashPasswordWhenCreatingAdmin() {
        when(findAdminByEmailPort.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        when(hashPasswordPort.hash("password123")).thenReturn("hashedPassword123");
        when(saveAdminPort.save(any(Admin.class))).thenReturn(testAdmin);

        adminService.create(createCommand);

        ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(saveAdminPort).save(adminCaptor.capture());

        Admin savedAdmin = adminCaptor.getValue();
        assertThat(savedAdmin.passwordHash()).isEqualTo("hashedPassword123");
        verify(hashPasswordPort).hash("password123");
    }

    @Test
    @DisplayName("Should get admin by id successfully")
    void shouldGetAdminByIdSuccessfully() {
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));

        Admin result = adminService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo(testAdmin.email());

        verify(loadAdminPort).loadById(1L);
    }

    @Test
    @DisplayName("Should throw AdminNotFoundException when admin not found by id")
    void shouldThrowExceptionWhenAdminNotFoundById() {
        when(loadAdminPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getById(999L))
                .isInstanceOf(AdminNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAdminPort).loadById(999L);
    }

    @Test
    @DisplayName("Should find admin by email successfully")
    void shouldFindAdminByEmailSuccessfully() {
        Email email = new Email("juan.garcia@example.com");
        when(findAdminByEmailPort.findByEmail(email)).thenReturn(Optional.of(testAdmin));

        Optional<Admin> result = adminService.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo(email);

        verify(findAdminByEmailPort).findByEmail(email);
    }

    @Test
    @DisplayName("Should return empty when admin not found by email")
    void shouldReturnEmptyWhenAdminNotFoundByEmail() {
        Email email = new Email("notfound@example.com");
        when(findAdminByEmailPort.findByEmail(email)).thenReturn(Optional.empty());

        Optional<Admin> result = adminService.findByEmail(email);

        assertThat(result).isEmpty();
        verify(findAdminByEmailPort).findByEmail(email);
    }

    @Test
    @DisplayName("Should update admin successfully")
    void shouldUpdateAdminSuccessfully() {
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(saveAdminPort.save(any(Admin.class))).thenReturn(testAdmin);

        verify(findAdminByEmailPort, never()).findByEmail(any(Email.class));

        Admin result = adminService.update(updateCommand);

        assertThat(result).isNotNull();
        verify(loadAdminPort).loadById(1L);
        verify(saveAdminPort).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent admin")
    void shouldThrowExceptionWhenUpdatingNonExistentAdmin() {
        when(loadAdminPort.loadById(999L)).thenReturn(Optional.empty());

        UpdateAdminCommand cmd = new UpdateAdminCommand(
                999L,
                updateCommand.name(),
                updateCommand.email(),
                updateCommand.phone()
        );

        assertThatThrownBy(() -> adminService.update(cmd))
                .isInstanceOf(AdminNotFoundException.class);

        verify(loadAdminPort).loadById(999L);
        verify(saveAdminPort, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should prevent email duplication when updating")
    void shouldPreventEmailDuplicationWhenUpdating() {
        Email newEmail = new Email("another@example.com");
        UpdateAdminCommand cmd = new UpdateAdminCommand(
                1L,
                updateCommand.name(),
                newEmail,
                updateCommand.phone()
        );

        Admin anotherAdmin = new Admin(
                2L,
                new PersonName("Pedro", "Sánchez"),
                newEmail,
                new Phone("+34687654321"),
                "hash",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(findAdminByEmailPort.findByEmail(newEmail)).thenReturn(Optional.of(anotherAdmin));

        assertThatThrownBy(() -> adminService.update(cmd))
                .isInstanceOf(AdminAlreadyExistsException.class);

        verify(saveAdminPort, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should allow same email when updating own record")
    void shouldAllowSameEmailWhenUpdatingOwnRecord() {
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(saveAdminPort.save(any(Admin.class))).thenReturn(testAdmin);

        verify(findAdminByEmailPort, never()).findByEmail(any(Email.class));

        assertThatCode(() -> adminService.update(updateCommand)).doesNotThrowAnyException();

        verify(saveAdminPort).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should activate inactive admin")
    void shouldActivateInactiveAdmin() {
        Admin inactiveAdmin = testAdmin.deactivate();
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(inactiveAdmin));
        when(saveAdminPort.save(any(Admin.class))).thenReturn(inactiveAdmin.activate());

        adminService.activate(1L);

        ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(saveAdminPort).save(adminCaptor.capture());

        Admin savedAdmin = adminCaptor.getValue();
        assertThat(savedAdmin.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should not save when admin is already active")
    void shouldNotSaveWhenAdminAlreadyActive() {
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));

        adminService.activate(1L);

        verify(loadAdminPort).loadById(1L);
        verify(saveAdminPort, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should deactivate active admin")
    void shouldDeactivateActiveAdmin() {
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(saveAdminPort.save(any(Admin.class))).thenReturn(testAdmin.deactivate());

        adminService.deactivate(1L);

        ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(saveAdminPort).save(adminCaptor.capture());

        Admin savedAdmin = adminCaptor.getValue();
        assertThat(savedAdmin.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should not save when admin is already inactive")
    void shouldNotSaveWhenAdminAlreadyInactive() {
        Admin inactiveAdmin = testAdmin.deactivate();
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(inactiveAdmin));

        adminService.deactivate(1L);

        verify(loadAdminPort).loadById(1L);
        verify(saveAdminPort, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should change password successfully")
    void shouldChangePasswordSuccessfully() {
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(testAdmin));
        when(hashPasswordPort.hash("newPassword123")).thenReturn("newHashedPassword");
        when(saveAdminPort.save(any(Admin.class))).thenReturn(testAdmin);

        adminService.changePassword(1L, "newPassword123");

        ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(saveAdminPort).save(adminCaptor.capture());

        Admin savedAdmin = adminCaptor.getValue();
        assertThat(savedAdmin.passwordHash()).isEqualTo("newHashedPassword");
        verify(hashPasswordPort).hash("newPassword123");
    }

    @Test
    @DisplayName("Should throw exception when password is too short")
    void shouldThrowExceptionWhenPasswordTooShort() {
        verify(loadAdminPort, never()).loadById(anyLong());

        assertThatThrownBy(() -> adminService.changePassword(1L, "short"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 8 characters");

        verify(hashPasswordPort, never()).hash(anyString());
        verify(saveAdminPort, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should throw exception when changing password for non-existent admin")
    void shouldThrowExceptionWhenChangingPasswordForNonExistentAdmin() {
        when(loadAdminPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.changePassword(999L, "newPassword123"))
                .isInstanceOf(AdminNotFoundException.class);

        verify(hashPasswordPort, never()).hash(anyString());
    }

    @Test
    @DisplayName("Should list all admins")
    void shouldListAllAdmins() {
        Admin admin2 = new Admin(
                2L,
                new PersonName("María", "Pérez"),
                new Email("maria@example.com"),
                new Phone("+34698765432"),
                "hash",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(listAdminsPort.findAll()).thenReturn(List.of(testAdmin, admin2));

        List<Admin> result = adminService.listAll();

        assertThat(result).hasSize(2);
        assertThat(result).contains(testAdmin, admin2);
        verify(listAdminsPort).findAll();
    }

    @Test
    @DisplayName("Should list only active admins")
    void shouldListOnlyActiveAdmins() {
        when(listAdminsPort.findAllActive()).thenReturn(List.of(testAdmin));

        List<Admin> result = adminService.listActive();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().isActive()).isTrue();
        verify(listAdminsPort).findAllActive();
    }

    @Test
    @DisplayName("Should return empty list when no admins exist")
    void shouldReturnEmptyListWhenNoAdminsExist() {
        when(listAdminsPort.findAll()).thenReturn(List.of());

        List<Admin> result = adminService.listAll();

        assertThat(result).isEmpty();
        verify(listAdminsPort).findAll();
    }

    @Test
    @DisplayName("Should throw exception when updating inactive admin")
    void shouldThrowExceptionWhenUpdatingInactiveAdmin() {
        Admin inactiveAdmin = testAdmin.deactivate();
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(inactiveAdmin));

        assertThatThrownBy(() -> adminService.update(updateCommand))
                .isInstanceOf(AdminNotActiveException.class)
                .hasMessageContaining("Admin is not active with id: 1");

        verify(saveAdminPort, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should throw exception when changing password for inactive admin")
    void shouldThrowExceptionWhenChangingPasswordForInactiveAdmin() {
        Admin inactiveAdmin = testAdmin.deactivate();
        when(loadAdminPort.loadById(1L)).thenReturn(Optional.of(inactiveAdmin));

        assertThatThrownBy(() -> adminService.changePassword(1L, "newPassword123"))
                .isInstanceOf(AdminNotActiveException.class)
                .hasMessageContaining("Admin is not active with id: 1");

        verify(hashPasswordPort, never()).hash(anyString());
        verify(saveAdminPort, never()).save(any(Admin.class));
    }
}
