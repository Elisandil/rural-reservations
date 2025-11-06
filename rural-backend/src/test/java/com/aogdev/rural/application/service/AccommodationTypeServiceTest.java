package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.out.accommodationType.*;
import com.aogdev.rural.domain.exception.accommodationType.AccommodationTypeAlreadyExistsException;
import com.aogdev.rural.domain.exception.accommodationType.AccommodationTypeNotFoundException;
import com.aogdev.rural.domain.model.AccommodationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccommodationTypeService Unit Tests")
class AccommodationTypeServiceTest {

    @Mock
    private SaveAccommodationTypePort saveAccommodationTypePort;

    @Mock
    private LoadAccommodationTypePort loadAccommodationTypePort;

    @Mock
    private FindAccommodationTypeByNamePort findAccommodationTypeByNamePort;

    @Mock
    private ListAccommodationTypesPort listAccommodationTypesPort;

    @Mock
    private DeleteAccommodationTypePort deleteAccommodationTypePort;

    private AccommodationTypeService accommodationTypeService;

    private AccommodationType testAccommodationType;

    @BeforeEach
    void setUp() {
        accommodationTypeService = new AccommodationTypeService(
                saveAccommodationTypePort,
                loadAccommodationTypePort,
                findAccommodationTypeByNamePort,
                listAccommodationTypesPort,
                deleteAccommodationTypePort
        );

        testAccommodationType = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo en casa rural tradicional"
        );
    }

    @Test
    @DisplayName("Should create accommodation type successfully when name is unique")
    void shouldCreateAccommodationTypeSuccessfully() {
        when(findAccommodationTypeByNamePort.findByName(anyString())).thenReturn(Optional.empty());
        when(saveAccommodationTypePort.save(any(AccommodationType.class))).thenReturn(testAccommodationType);

        AccommodationType result = accommodationTypeService.create("Casa Rural", "Alojamiento completo en casa rural tradicional");

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Casa Rural");
        assertThat(result.description()).isEqualTo("Alojamiento completo en casa rural tradicional");
        assertThat(result.id()).isEqualTo((short) 1);

        verify(findAccommodationTypeByNamePort).findByName("Casa Rural");
        verify(saveAccommodationTypePort).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should create accommodation type with null description")
    void shouldCreateAccommodationTypeWithNullDescription() {
        AccommodationType typeWithoutDescription = new AccommodationType((short) 1, "Apartamento", null);
        when(findAccommodationTypeByNamePort.findByName("Apartamento")).thenReturn(Optional.empty());
        when(saveAccommodationTypePort.save(any(AccommodationType.class))).thenReturn(typeWithoutDescription);

        AccommodationType result = accommodationTypeService.create("Apartamento", null);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Apartamento");
        assertThat(result.description()).isNull();

        verify(saveAccommodationTypePort).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should throw AccommodationTypeAlreadyExistsException when name is duplicate")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        when(findAccommodationTypeByNamePort.findByName("Casa Rural")).thenReturn(Optional.of(testAccommodationType));

        assertThatThrownBy(() -> accommodationTypeService.create("Casa Rural", "Descripción"))
                .isInstanceOf(AccommodationTypeAlreadyExistsException.class)
                .hasMessageContaining("Casa Rural");

        verify(findAccommodationTypeByNamePort).findByName("Casa Rural");
        verify(saveAccommodationTypePort, never()).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThatThrownBy(() -> accommodationTypeService.create(null, "Descripción"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be null or blank");

        verify(findAccommodationTypeByNamePort, never()).findByName(anyString());
        verify(saveAccommodationTypePort, never()).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThatThrownBy(() -> accommodationTypeService.create("   ", "Descripción"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be null or blank");

        verify(findAccommodationTypeByNamePort, never()).findByName(anyString());
        verify(saveAccommodationTypePort, never()).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should save accommodation type with null id when creating")
    void shouldSaveAccommodationTypeWithNullId() {
        when(findAccommodationTypeByNamePort.findByName("Bungalow")).thenReturn(Optional.empty());
        when(saveAccommodationTypePort.save(any(AccommodationType.class))).thenReturn(testAccommodationType);

        accommodationTypeService.create("Bungalow", "Descripción");

        ArgumentCaptor<AccommodationType> captor = ArgumentCaptor.forClass(AccommodationType.class);
        verify(saveAccommodationTypePort).save(captor.capture());

        AccommodationType savedType = captor.getValue();
        assertThat(savedType.id()).isNull();
        assertThat(savedType.name()).isEqualTo("Bungalow");
    }

    @Test
    @DisplayName("Should get accommodation type by id successfully")
    void shouldGetAccommodationTypeByIdSuccessfully() {
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));

        AccommodationType result = accommodationTypeService.getById((short) 1);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo((short) 1);
        assertThat(result.name()).isEqualTo("Casa Rural");

        verify(loadAccommodationTypePort).loadById((short) 1);
    }

    @Test
    @DisplayName("Should throw AccommodationTypeNotFoundException when accommodation type not found by id")
    void shouldThrowExceptionWhenAccommodationTypeNotFoundById() {
        when(loadAccommodationTypePort.loadById((short) 999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationTypeService.getById((short) 999))
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationTypePort).loadById((short) 999);
    }

    @Test
    @DisplayName("Should find accommodation type by name successfully")
    void shouldFindAccommodationTypeByNameSuccessfully() {
        when(findAccommodationTypeByNamePort.findByName("Casa Rural")).thenReturn(Optional.of(testAccommodationType));

        Optional<AccommodationType> result = accommodationTypeService.findByName("Casa Rural");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Casa Rural");

        verify(findAccommodationTypeByNamePort).findByName("Casa Rural");
    }

    @Test
    @DisplayName("Should return empty when accommodation type not found by name")
    void shouldReturnEmptyWhenAccommodationTypeNotFoundByName() {
        when(findAccommodationTypeByNamePort.findByName("Inexistente")).thenReturn(Optional.empty());

        Optional<AccommodationType> result = accommodationTypeService.findByName("Inexistente");

        assertThat(result).isEmpty();
        verify(findAccommodationTypeByNamePort).findByName("Inexistente");
    }

    @Test
    @DisplayName("Should update accommodation type successfully")
    void shouldUpdateAccommodationTypeSuccessfully() {
        AccommodationType updatedType = new AccommodationType((short) 1, "Casa Rural Premium", "Descripción actualizada");
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationTypePort.save(any(AccommodationType.class))).thenReturn(updatedType);

        AccommodationType result = accommodationTypeService.update((short) 1, "Casa Rural Premium", "Descripción actualizada");

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Casa Rural Premium");
        assertThat(result.description()).isEqualTo("Descripción actualizada");

        verify(loadAccommodationTypePort).loadById((short) 1);
        verify(saveAccommodationTypePort).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent accommodation type")
    void shouldThrowExceptionWhenUpdatingNonExistentAccommodationType() {
        when(loadAccommodationTypePort.loadById((short) 999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationTypeService.update((short) 999, "Nombre", "Descripción"))
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationTypePort).loadById((short) 999);
        verify(saveAccommodationTypePort, never()).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with null id")
    void shouldThrowExceptionWhenUpdatingWithNullId() {
        assertThatThrownBy(() -> accommodationTypeService.update(null, "Nombre", "Descripción"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Accommodation type ID cannot be null");

        verify(loadAccommodationTypePort, never()).loadById(any());
        verify(saveAccommodationTypePort, never()).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with null name")
    void shouldThrowExceptionWhenUpdatingWithNullName() {
        assertThatThrownBy(() -> accommodationTypeService.update((short) 1, null, "Descripción"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be null or blank");

        verify(loadAccommodationTypePort, never()).loadById(any());
        verify(saveAccommodationTypePort, never()).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with blank name")
    void shouldThrowExceptionWhenUpdatingWithBlankName() {
        assertThatThrownBy(() -> accommodationTypeService.update((short) 1, "   ", "Descripción"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be null or blank");

        verify(loadAccommodationTypePort, never()).loadById(any());
        verify(saveAccommodationTypePort, never()).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should prevent name duplication when updating")
    void shouldPreventNameDuplicationWhenUpdating() {
        AccommodationType anotherType = new AccommodationType((short) 2, "Apartamento", "Otro tipo");
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));
        when(findAccommodationTypeByNamePort.findByName("Apartamento")).thenReturn(Optional.of(anotherType));

        assertThatThrownBy(() -> accommodationTypeService.update((short) 1, "Apartamento", "Nueva descripción"))
                .isInstanceOf(AccommodationTypeAlreadyExistsException.class)
                .hasMessageContaining("Apartamento");

        verify(saveAccommodationTypePort, never()).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should allow same name when updating own record")
    void shouldAllowSameNameWhenUpdatingOwnRecord() {
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationTypePort.save(any(AccommodationType.class))).thenReturn(testAccommodationType);

        assertThatCode(() -> accommodationTypeService.update((short) 1, "Casa Rural", "Nueva descripción"))
                .doesNotThrowAnyException();

        verify(saveAccommodationTypePort).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should update accommodation type when changing name to unique one")
    void shouldUpdateWhenChangingNameToUniqueOne() {
        AccommodationType updatedType = new AccommodationType((short) 1, "Villa Rural", "Nueva descripción");
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));
        when(findAccommodationTypeByNamePort.findByName("Villa Rural")).thenReturn(Optional.empty());
        when(saveAccommodationTypePort.save(any(AccommodationType.class))).thenReturn(updatedType);

        AccommodationType result = accommodationTypeService.update((short) 1, "Villa Rural", "Nueva descripción");

        assertThat(result.name()).isEqualTo("Villa Rural");
        verify(findAccommodationTypeByNamePort).findByName("Villa Rural");
        verify(saveAccommodationTypePort).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should delete accommodation type successfully")
    void shouldDeleteAccommodationTypeSuccessfully() {
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));
        doNothing().when(deleteAccommodationTypePort).deleteById((short) 1);

        accommodationTypeService.delete((short) 1);

        verify(loadAccommodationTypePort).loadById((short) 1);
        verify(deleteAccommodationTypePort).deleteById((short) 1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent accommodation type")
    void shouldThrowExceptionWhenDeletingNonExistentAccommodationType() {
        when(loadAccommodationTypePort.loadById((short) 999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationTypeService.delete((short) 999))
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadAccommodationTypePort).loadById((short) 999);
        verify(deleteAccommodationTypePort, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should list all accommodation types")
    void shouldListAllAccommodationTypes() {
        AccommodationType type2 = new AccommodationType((short) 2, "Apartamento", "Apartamento turístico");
        AccommodationType type3 = new AccommodationType((short) 3, "Bungalow", "Bungalow con jardín");

        when(listAccommodationTypesPort.findAll()).thenReturn(List.of(testAccommodationType, type2, type3));

        List<AccommodationType> result = accommodationTypeService.listAll();

        assertThat(result).hasSize(3);
        assertThat(result).contains(testAccommodationType, type2, type3);
        verify(listAccommodationTypesPort).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no accommodation types exist")
    void shouldReturnEmptyListWhenNoAccommodationTypesExist() {
        when(listAccommodationTypesPort.findAll()).thenReturn(List.of());

        List<AccommodationType> result = accommodationTypeService.listAll();

        assertThat(result).isEmpty();
        verify(listAccommodationTypesPort).findAll();
    }

    @Test
    @DisplayName("Should update accommodation type with null description")
    void shouldUpdateAccommodationTypeWithNullDescription() {
        AccommodationType updatedType = new AccommodationType((short) 1, "Casa Rural", null);
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));
        when(saveAccommodationTypePort.save(any(AccommodationType.class))).thenReturn(updatedType);

        AccommodationType result = accommodationTypeService.update((short) 1, "Casa Rural", null);

        assertThat(result.description()).isNull();
        verify(saveAccommodationTypePort).save(any(AccommodationType.class));
    }

    @Test
    @DisplayName("Should preserve id when updating")
    void shouldPreserveIdWhenUpdating() {
        AccommodationType updatedType = new AccommodationType((short) 1, "Nuevo Nombre", "Nueva descripción");
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.of(testAccommodationType));
        when(findAccommodationTypeByNamePort.findByName("Nuevo Nombre")).thenReturn(Optional.empty());
        when(saveAccommodationTypePort.save(any(AccommodationType.class))).thenReturn(updatedType);

        accommodationTypeService.update((short) 1, "Nuevo Nombre", "Nueva descripción");

        ArgumentCaptor<AccommodationType> captor = ArgumentCaptor.forClass(AccommodationType.class);
        verify(saveAccommodationTypePort).save(captor.capture());

        AccommodationType savedType = captor.getValue();
        assertThat(savedType.id()).isEqualTo((short) 1);
        assertThat(savedType.name()).isEqualTo("Nuevo Nombre");
        assertThat(savedType.description()).isEqualTo("Nueva descripción");
    }
}