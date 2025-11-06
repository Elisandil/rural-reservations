package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AccommodationTypeJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationTypeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccommodationTypePersistenceAdapter Tests")
class AccommodationTypePersistenceAdapterTest {

    @Mock
    private AccommodationTypeJpaRepository repository;

    @InjectMocks
    private AccommodationTypePersistenceAdapter adapter;

    private AccommodationType testAccommodationType;
    private AccommodationTypeJpaEntity testEntity;

    @BeforeEach
    void setUp() {
        testAccommodationType = new AccommodationType(
                (short) 1,
                "Casa Rural",
                "Alojamiento completo en casa rural tradicional"
        );

        testEntity = AccommodationTypeJpaEntity.builder()
                .id((short) 1)
                .name("Casa Rural")
                .description("Alojamiento completo en casa rural tradicional")
                .build();
    }

    @Test
    @DisplayName("Should save accommodation type successfully")
    void shouldSaveAccommodationTypeSuccessfully() {
        when(repository.save(any(AccommodationTypeJpaEntity.class))).thenReturn(testEntity);

        AccommodationType result = adapter.save(testAccommodationType);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo((short) 1);
        assertThat(result.name()).isEqualTo("Casa Rural");
        assertThat(result.description()).isEqualTo("Alojamiento completo en casa rural tradicional");

        verify(repository).save(any(AccommodationTypeJpaEntity.class));
    }

    @Test
    @DisplayName("Should save accommodation type with null description")
    void shouldSaveAccommodationTypeWithNullDescription() {
        AccommodationType typeWithoutDescription = new AccommodationType((short) 1, "Apartamento", null);
        AccommodationTypeJpaEntity entityWithoutDescription = AccommodationTypeJpaEntity.builder()
                .id((short) 1)
                .name("Apartamento")
                .description(null)
                .build();

        when(repository.save(any(AccommodationTypeJpaEntity.class))).thenReturn(entityWithoutDescription);

        AccommodationType result = adapter.save(typeWithoutDescription);

        assertThat(result).isNotNull();
        assertThat(result.description()).isNull();

        verify(repository).save(any(AccommodationTypeJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle save error gracefully")
    void shouldHandleSaveErrorGracefully() {
        when(repository.save(any(AccommodationTypeJpaEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> adapter.save(testAccommodationType))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to save accommodation type");

        verify(repository).save(any(AccommodationTypeJpaEntity.class));
    }

    @Test
    @DisplayName("Should load accommodation type by id successfully")
    void shouldLoadAccommodationTypeByIdSuccessfully() {
        when(repository.findById((short) 1)).thenReturn(Optional.of(testEntity));

        Optional<AccommodationType> result = adapter.loadById((short) 1);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo((short) 1);
        assertThat(result.get().name()).isEqualTo("Casa Rural");

        verify(repository).findById((short) 1);
    }

    @Test
    @DisplayName("Should return empty when accommodation type not found by id")
    void shouldReturnEmptyWhenAccommodationTypeNotFoundById() {
        when(repository.findById((short) 999)).thenReturn(Optional.empty());

        Optional<AccommodationType> result = adapter.loadById((short) 999);

        assertThat(result).isEmpty();
        verify(repository).findById((short) 999);
    }

    @Test
    @DisplayName("Should find accommodation type by name successfully")
    void shouldFindAccommodationTypeByNameSuccessfully() {
        when(repository.findByName("Casa Rural")).thenReturn(Optional.of(testEntity));

        Optional<AccommodationType> result = adapter.findByName("Casa Rural");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Casa Rural");

        verify(repository).findByName("Casa Rural");
    }

    @Test
    @DisplayName("Should return empty when accommodation type not found by name")
    void shouldReturnEmptyWhenAccommodationTypeNotFoundByName() {
        when(repository.findByName("Inexistente")).thenReturn(Optional.empty());

        Optional<AccommodationType> result = adapter.findByName("Inexistente");

        assertThat(result).isEmpty();
        verify(repository).findByName("Inexistente");
    }

    @Test
    @DisplayName("Should find all accommodation types successfully")
    void shouldFindAllAccommodationTypesSuccessfully() {
        AccommodationTypeJpaEntity entity2 = AccommodationTypeJpaEntity.builder()
                .id((short) 2)
                .name("Apartamento")
                .description("Apartamento turístico")
                .build();

        AccommodationTypeJpaEntity entity3 = AccommodationTypeJpaEntity.builder()
                .id((short) 3)
                .name("Bungalow")
                .description("Bungalow con jardín")
                .build();

        when(repository.findAll()).thenReturn(List.of(testEntity, entity2, entity3));

        List<AccommodationType> result = adapter.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo((short) 1);
        assertThat(result.get(1).id()).isEqualTo((short) 2);
        assertThat(result.get(2).id()).isEqualTo((short) 3);

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no accommodation types exist")
    void shouldReturnEmptyListWhenNoAccommodationTypesExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<AccommodationType> result = adapter.findAll();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should delete accommodation type by id successfully")
    void shouldDeleteAccommodationTypeByIdSuccessfully() {
        doNothing().when(repository).deleteById((short) 1);

        assertThatCode(() -> adapter.deleteById((short) 1))
                .doesNotThrowAnyException();

        verify(repository).deleteById((short) 1);
    }

    @Test
    @DisplayName("Should handle delete error gracefully")
    void shouldHandleDeleteErrorGracefully() {
        doThrow(new RuntimeException("Database error"))
                .when(repository).deleteById((short) 1);

        assertThatThrownBy(() -> adapter.deleteById((short) 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to delete accommodation type");

        verify(repository).deleteById((short) 1);
    }

    @Test
    @DisplayName("Should preserve data integrity when saving")
    void shouldPreserveDataIntegrityWhenSaving() {
        AccommodationType type = new AccommodationType(
                null,
                "Nueva Casa",
                "Descripción detallada"
        );

        AccommodationTypeJpaEntity savedEntity = AccommodationTypeJpaEntity.builder()
                .id((short) 5)
                .name("Nueva Casa")
                .description("Descripción detallada")
                .build();

        when(repository.save(any(AccommodationTypeJpaEntity.class))).thenReturn(savedEntity);

        AccommodationType result = adapter.save(type);

        assertThat(result.id()).isEqualTo((short) 5);
        assertThat(result.name()).isEqualTo("Nueva Casa");
        assertThat(result.description()).isEqualTo("Descripción detallada");
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void shouldHandleSpecialCharactersInName() {
        AccommodationType type = new AccommodationType(
                (short) 1,
                "Casa «El Olivo»",
                "Con carácter especial"
        );

        AccommodationTypeJpaEntity entity = AccommodationTypeJpaEntity.builder()
                .id((short) 1)
                .name("Casa «El Olivo»")
                .description("Con carácter especial")
                .build();

        when(repository.save(any(AccommodationTypeJpaEntity.class))).thenReturn(entity);

        AccommodationType result = adapter.save(type);

        assertThat(result.name()).isEqualTo("Casa «El Olivo»");
        verify(repository).save(any(AccommodationTypeJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle very long descriptions")
    void shouldHandleVeryLongDescriptions() {
        String longDescription = "A".repeat(1000);
        AccommodationType type = new AccommodationType((short) 1, "Casa Grande", longDescription);

        AccommodationTypeJpaEntity entity = AccommodationTypeJpaEntity.builder()
                .id((short) 1)
                .name("Casa Grande")
                .description(longDescription)
                .build();

        when(repository.save(any(AccommodationTypeJpaEntity.class))).thenReturn(entity);

        AccommodationType result = adapter.save(type);

        assertThat(result.description()).hasSize(1000);
        verify(repository).save(any(AccommodationTypeJpaEntity.class));
    }

    @Test
    @DisplayName("Should maintain list order when finding all")
    void shouldMaintainListOrderWhenFindingAll() {
        List<AccommodationTypeJpaEntity> entities = List.of(
                AccommodationTypeJpaEntity.builder().id((short) 3).name("C").description("Third").build(),
                AccommodationTypeJpaEntity.builder().id((short) 1).name("A").description("First").build(),
                AccommodationTypeJpaEntity.builder().id((short) 2).name("B").description("Second").build()
        );

        when(repository.findAll()).thenReturn(entities);

        List<AccommodationType> result = adapter.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo((short) 3);
        assertThat(result.get(1).id()).isEqualTo((short) 1);
        assertThat(result.get(2).id()).isEqualTo((short) 2);
    }
}