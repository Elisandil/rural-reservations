package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.accommodation.CreateAccommodationCommand;
import com.aogdev.rural.application.port.in.accommodation.UpdateAccommodationCommand;
import com.aogdev.rural.application.port.out.accommodation.*;
import com.aogdev.rural.application.port.out.accommodationType.LoadAccommodationTypePort;
import com.aogdev.rural.domain.exception.accommodation.AccommodationAlreadyExistsException;
import com.aogdev.rural.domain.exception.accommodation.AccommodationNotActiveException;
import com.aogdev.rural.domain.exception.accommodation.AccommodationNotFoundException;
import com.aogdev.rural.domain.exception.accommodationType.AccommodationTypeNotFoundException;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccommodationService Unit Tests")
class AccommodationServiceTest {

    @Mock
    private SaveAccommodationPort saveAccommodationPort;

    @Mock
    private LoadAccommodationPort loadAccommodationPort;

    @Mock
    private FindAccommodationByNamePort findAccommodationByNamePort;

    @Mock
    private ListAccommodationsPort listAccommodationsPort;

    @Mock
    private LoadAccommodationTypePort loadAccommodationTypePort;

    private AccommodationService accommodationService;

    private AccommodationType testAccommodationType;
    private Accommodation testAccommodation;
    private CreateAccommodationCommand testCreateCommand;
    private UpdateAccommodationCommand testUpdateCommand;

    @BeforeEach
    void setUp() {
        accommodationService = new AccommodationService(
                saveAccommodationPort,
                loadAccommodationPort,
                findAccommodationByNamePort,
                listAccommodationsPort,
                loadAccommodationTypePort
        );

        testAccommodationType = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo en casa rural tradicional"
        );

        testAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        testCreateCommand = new CreateAccommodationCommand(
                (short) 1,
                "Casa del Bosque",
                Money.euros(85.00),
                4
        );

        testUpdateCommand = new UpdateAccommodationCommand(
                1L,
                (short) 1,
                "Casa del Bosque",
                Money.euros(85.00),
                4
        );
    }

    @Test
    @DisplayName("Should create accommodation successfully when name is unique")
    void shouldCreateAccommodationSuccessfully() {
        when(findAccommodationByNamePort.findByName("Casa del Bosque"))
                .thenReturn(Optional.empty());
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationPort.save(any(Accommodation.class)))
                .thenReturn(testAccommodation);

        Accommodation result = accommodationService.create(testCreateCommand);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Casa del Bosque");
        assertThat(result.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("85.00"));
        assertThat(result.bedCapacity()).isEqualTo(4);
        assertThat(result.isActive()).isTrue();

        verify(findAccommodationByNamePort).findByName("Casa del Bosque");
        verify(loadAccommodationTypePort).loadById((short) 1);
        verify(saveAccommodationPort).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should throw AccommodationAlreadyExistsException when name is duplicate")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        when(findAccommodationByNamePort.findByName("Casa del Bosque"))
                .thenReturn(Optional.of(testAccommodation));

        assertThatThrownBy(() -> accommodationService.create(testCreateCommand))
                .isInstanceOf(AccommodationAlreadyExistsException.class)
                .hasMessageContaining("Casa del Bosque");

        verify(findAccommodationByNamePort).findByName("Casa del Bosque");
        verify(loadAccommodationTypePort, never()).loadById(any());
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should throw AccommodationTypeNotFoundException when accommodation type does not exist")
    void shouldThrowExceptionWhenAccommodationTypeNotFound() {
        when(findAccommodationByNamePort.findByName("Casa del Bosque"))
                .thenReturn(Optional.empty());
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService.create(testCreateCommand))
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("1");

        verify(findAccommodationByNamePort).findByName("Casa del Bosque");
        verify(loadAccommodationTypePort).loadById((short) 1);
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should save accommodation with null id and active true when creating")
    void shouldSaveAccommodationWithNullIdAndActiveTrue() {
        when(findAccommodationByNamePort.findByName("Casa del Bosque")).thenReturn(Optional.empty());
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationPort.save(any(Accommodation.class)))
                .thenReturn(testAccommodation);

        accommodationService.create(testCreateCommand);

        ArgumentCaptor<Accommodation> captor = ArgumentCaptor.forClass(Accommodation.class);
        verify(saveAccommodationPort).save(captor.capture());

        Accommodation savedAccommodation = captor.getValue();
        assertThat(savedAccommodation.id()).isNull();
        assertThat(savedAccommodation.name()).isEqualTo("Casa del Bosque");
        assertThat(savedAccommodation.active()).isTrue();
    }

    @Test
    @DisplayName("Should get accommodation by id successfully")
    void shouldGetAccommodationByIdSuccessfully() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));

        Accommodation result = accommodationService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Casa del Bosque");

        verify(loadAccommodationPort).loadById(1L);
    }

    @Test
    @DisplayName("Should throw AccommodationNotFoundException when accommodation not found by id")
    void shouldThrowExceptionWhenAccommodationNotFoundById() {
        when(loadAccommodationPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService.getById(999L))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationPort).loadById(999L);
    }

    @Test
    @DisplayName("Should find accommodation by name successfully")
    void shouldFindAccommodationByNameSuccessfully() {
        when(findAccommodationByNamePort.findByName("Casa del Bosque"))
                .thenReturn(Optional.of(testAccommodation));

        Optional<Accommodation> result = accommodationService.findByName("Casa del Bosque");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Casa del Bosque");

        verify(findAccommodationByNamePort).findByName("Casa del Bosque");
    }

    @Test
    @DisplayName("Should return empty when accommodation not found by name")
    void shouldReturnEmptyWhenAccommodationNotFoundByName() {
        when(findAccommodationByNamePort.findByName("Inexistente")).thenReturn(Optional.empty());

        Optional<Accommodation> result = accommodationService.findByName("Inexistente");

        assertThat(result).isEmpty();
        verify(findAccommodationByNamePort).findByName("Inexistente");
    }

    @Test
    @DisplayName("Should update accommodation successfully")
    void shouldUpdateAccommodationSuccessfully() {
        Accommodation updatedAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa del Bosque",
                Money.euros(95.00),
                6,
                true
        );

        UpdateAccommodationCommand updateCommand = new UpdateAccommodationCommand(
                1L,
                (short) 1,
                "Casa del Bosque",
                Money.euros(95.00),
                6
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationPort.save(any(Accommodation.class)))
                .thenReturn(updatedAccommodation);

        Accommodation result = accommodationService.update(updateCommand);

        assertThat(result).isNotNull();
        assertThat(result.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("95.00"));
        assertThat(result.bedCapacity()).isEqualTo(6);

        verify(loadAccommodationPort).loadById(1L);
        verify(loadAccommodationTypePort).loadById((short) 1);
        verify(saveAccommodationPort).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent accommodation")
    void shouldThrowExceptionWhenUpdatingNonExistentAccommodation() {
        when(loadAccommodationPort.loadById(999L)).thenReturn(Optional.empty());

        UpdateAccommodationCommand command = new UpdateAccommodationCommand(
                999L,
                (short) 1,
                "Casa del Bosque",
                Money.euros(85.00),
                4
        );

        assertThatThrownBy(() -> accommodationService.update(command))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationPort).loadById(999L);
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should throw AccommodationNotActiveException when updating inactive accommodation")
    void shouldThrowExceptionWhenUpdatingInactiveAccommodation() {
        Accommodation inactiveAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                false
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(inactiveAccommodation));

        assertThatThrownBy(() -> accommodationService.update(testUpdateCommand))
                .isInstanceOf(AccommodationNotActiveException.class)
                .hasMessageContaining("1");

        verify(loadAccommodationPort).loadById(1L);
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should throw AccommodationTypeNotFoundException when updating with non-existent type")
    void shouldThrowExceptionWhenUpdatingWithNonExistentType() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService.update(testUpdateCommand))
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("1");

        verify(loadAccommodationPort).loadById(1L);
        verify(loadAccommodationTypePort).loadById((short) 1);
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should prevent name duplication when updating")
    void shouldPreventNameDuplicationWhenUpdating() {
        Accommodation anotherAccommodation = new Accommodation(
                2L,
                testAccommodationType,
                "Villa del Mar",
                Money.euros(100.00),
                5,
                true
        );

        UpdateAccommodationCommand command = new UpdateAccommodationCommand(
                1L,
                (short) 1,
                "Villa del Mar",
                Money.euros(85.00),
                4
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(findAccommodationByNamePort.findByName("Villa del Mar"))
                .thenReturn(Optional.of(anotherAccommodation));

        assertThatThrownBy(() -> accommodationService.update(command))
                .isInstanceOf(AccommodationAlreadyExistsException.class)
                .hasMessageContaining("Villa del Mar");

        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should allow same name when updating own record")
    void shouldAllowSameNameWhenUpdatingOwnRecord() {
        when(loadAccommodationPort.loadById(1L))
                .thenReturn(Optional.of(testAccommodation));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationPort.save(any(Accommodation.class)))
                .thenReturn(testAccommodation);

        assertThatCode(() -> accommodationService.update(testUpdateCommand))
                .doesNotThrowAnyException();

        verify(saveAccommodationPort).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should update accommodation when changing name to unique one")
    void shouldUpdateWhenChangingNameToUniqueOne() {
        Accommodation updatedAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa Rural Premium",
                Money.euros(85.00),
                4,
                true
        );

        UpdateAccommodationCommand command = new UpdateAccommodationCommand(
                1L,
                (short) 1,
                "Casa Rural Premium",
                Money.euros(85.00),
                4
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(findAccommodationByNamePort.findByName("Casa Rural Premium"))
                .thenReturn(Optional.empty());
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationPort.save(any(Accommodation.class)))
                .thenReturn(updatedAccommodation);

        Accommodation result = accommodationService.update(command);

        assertThat(result.name()).isEqualTo("Casa Rural Premium");
        verify(findAccommodationByNamePort).findByName("Casa Rural Premium");
        verify(saveAccommodationPort).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should preserve active status when updating")
    void shouldPreserveActiveStatusWhenUpdating() {
        when(loadAccommodationPort.loadById(1L))
                .thenReturn(Optional.of(testAccommodation));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationPort.save(any(Accommodation.class)))
                .thenReturn(testAccommodation);

        accommodationService.update(testUpdateCommand);

        ArgumentCaptor<Accommodation> captor = ArgumentCaptor.forClass(Accommodation.class);
        verify(saveAccommodationPort).save(captor.capture());

        Accommodation savedAccommodation = captor.getValue();
        assertThat(savedAccommodation.active()).isEqualTo(testAccommodation.active());
    }

    @Test
    @DisplayName("Should activate accommodation successfully")
    void shouldActivateAccommodationSuccessfully() {
        Accommodation inactiveAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                false
        );

        Accommodation activatedAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                true
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(inactiveAccommodation));
        when(saveAccommodationPort.save(any(Accommodation.class)))
                .thenReturn(activatedAccommodation);

        accommodationService.activate(1L);

        ArgumentCaptor<Accommodation> captor = ArgumentCaptor.forClass(Accommodation.class);
        verify(saveAccommodationPort).save(captor.capture());

        Accommodation savedAccommodation = captor.getValue();
        assertThat(savedAccommodation.active()).isTrue();
    }

    @Test
    @DisplayName("Should not save when activating already active accommodation")
    void shouldNotSaveWhenActivatingAlreadyActiveAccommodation() {
        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));

        accommodationService.activate(1L);

        verify(loadAccommodationPort).loadById(1L);
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should throw exception when activating non-existent accommodation")
    void shouldThrowExceptionWhenActivatingNonExistentAccommodation() {
        when(loadAccommodationPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService.activate(999L))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationPort).loadById(999L);
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should deactivate accommodation successfully")
    void shouldDeactivateAccommodationSuccessfully() {
        Accommodation deactivatedAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                false
        );

        when(loadAccommodationPort.loadById(1L)).thenReturn(Optional.of(testAccommodation));
        when(saveAccommodationPort.save(any(Accommodation.class)))
                .thenReturn(deactivatedAccommodation);

        accommodationService.deactivate(1L);

        ArgumentCaptor<Accommodation> captor = ArgumentCaptor.forClass(Accommodation.class);
        verify(saveAccommodationPort).save(captor.capture());

        Accommodation savedAccommodation = captor.getValue();
        assertThat(savedAccommodation.active()).isFalse();
    }

    @Test
    @DisplayName("Should not save when deactivating already inactive accommodation")
    void shouldNotSaveWhenDeactivatingAlreadyInactiveAccommodation() {
        Accommodation inactiveAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                false
        );

        when(loadAccommodationPort.loadById(1L))
                .thenReturn(Optional.of(inactiveAccommodation));

        accommodationService.deactivate(1L);

        verify(loadAccommodationPort).loadById(1L);
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should throw exception when deactivating non-existent accommodation")
    void shouldThrowExceptionWhenDeactivatingNonExistentAccommodation() {
        when(loadAccommodationPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService
                .deactivate(999L))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationPort).loadById(999L);
        verify(saveAccommodationPort, never()).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should list all accommodations")
    void shouldListAllAccommodations() {
        Accommodation accommodation2 = new Accommodation(
                2L,
                testAccommodationType,
                "Villa del Mar",
                Money.euros(120.00),
                6,
                true
        );

        Accommodation accommodation3 = new Accommodation(
                3L,
                testAccommodationType,
                "Apartamento Centro",
                Money.euros(60.00),
                2,
                false
        );

        when(listAccommodationsPort.findAll())
                .thenReturn(List.of(testAccommodation, accommodation2, accommodation3));

        List<Accommodation> result = accommodationService.listAll();

        assertThat(result).hasSize(3);
        assertThat(result).contains(testAccommodation, accommodation2, accommodation3);
        verify(listAccommodationsPort).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no accommodations exist")
    void shouldReturnEmptyListWhenNoAccommodationsExist() {
        when(listAccommodationsPort.findAll()).thenReturn(List.of());

        List<Accommodation> result = accommodationService.listAll();

        assertThat(result).isEmpty();
        verify(listAccommodationsPort).findAll();
    }

    @Test
    @DisplayName("Should list active accommodations only")
    void shouldListActiveAccommodationsOnly() {
        Accommodation accommodation2 = new Accommodation(
                2L,
                testAccommodationType,
                "Villa del Mar",
                Money.euros(120.00),
                6,
                true
        );

        when(listAccommodationsPort.findAllActive())
                .thenReturn(List.of(testAccommodation, accommodation2));

        List<Accommodation> result = accommodationService.listActive();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Accommodation::isActive);
        verify(listAccommodationsPort).findAllActive();
    }

    @Test
    @DisplayName("Should list accommodations by accommodation type")
    void shouldListAccommodationsByAccommodationType() {
        Accommodation accommodation2 = new Accommodation(
                2L,
                testAccommodationType,
                "Villa del Mar",
                Money.euros(120.00),
                6,
                true
        );

        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));
        when(listAccommodationsPort.findByAccommodationTypeId((short) 1))
                .thenReturn(List.of(testAccommodation, accommodation2));

        List<Accommodation> result = accommodationService.listByAccommodationType((short) 1);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(acc -> acc.accommodationType().id().equals((short) 1));
        verify(loadAccommodationTypePort).loadById((short) 1);
        verify(listAccommodationsPort).findByAccommodationTypeId((short) 1);
    }

    @Test
    @DisplayName("Should throw exception when listing by non-existent accommodation type")
    void shouldThrowExceptionWhenListingByNonExistentAccommodationType() {
        when(loadAccommodationTypePort.loadById((short) 999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService
                .listByAccommodationType((short) 999))
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationTypePort).loadById((short) 999);
        verify(listAccommodationsPort, never()).findByAccommodationTypeId(any());
    }

    @Test
    @DisplayName("Should return empty list when no accommodations of specific type exist")
    void shouldReturnEmptyListWhenNoAccommodationsOfSpecificTypeExist() {
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));
        when(listAccommodationsPort.findByAccommodationTypeId((short) 1)).thenReturn(List.of());

        List<Accommodation> result = accommodationService.listByAccommodationType((short) 1);

        assertThat(result).isEmpty();
        verify(listAccommodationsPort).findByAccommodationTypeId((short) 1);
    }
}