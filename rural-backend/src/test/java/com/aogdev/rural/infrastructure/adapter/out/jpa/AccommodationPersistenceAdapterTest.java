package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.application.port.out.accommodationType.LoadAccommodationTypePort;
import com.aogdev.rural.domain.exception.accommodationType.AccommodationTypeNotFoundException;
import com.aogdev.rural.domain.model.Accommodation;
import com.aogdev.rural.domain.model.AccommodationType;
import com.aogdev.rural.domain.valueobject.Money;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AccommodationJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.AccommodationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccommodationPersistenceAdapter Tests")
class AccommodationPersistenceAdapterTest {

    @Mock
    private AccommodationJpaRepository repository;

    @Mock
    private LoadAccommodationTypePort loadAccommodationTypePort;

    @InjectMocks
    private AccommodationPersistenceAdapter adapter;

    private AccommodationType testAccommodationType;
    private Accommodation testAccommodation;
    private AccommodationJpaEntity testEntity;

    @BeforeEach
    void setUp() {
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

        testEntity = AccommodationJpaEntity.builder()
                .id(1L)
                .accommodationTypeId((short) 1)
                .name("Casa del Bosque")
                .pricePerNight(new BigDecimal("85.00"))
                .currency("EUR")
                .bedCapacity(4)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should save accommodation successfully")
    void shouldSaveAccommodationSuccessfully() {
        when(repository.save(any(AccommodationJpaEntity.class))).thenReturn(testEntity);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Accommodation result = adapter.save(testAccommodation);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Casa del Bosque");
        assertThat(result.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("85.00"));
        assertThat(result.bedCapacity()).isEqualTo(4);
        assertThat(result.isActive()).isTrue();

        verify(repository).save(any(AccommodationJpaEntity.class));
        verify(loadAccommodationTypePort).loadById((short) 1);
    }

    @Test
    @DisplayName("Should save accommodation with inactive status")
    void shouldSaveAccommodationWithInactiveStatus() {
        Accommodation inactiveAccommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa del Bosque",
                Money.euros(85.00),
                4,
                false
        );

        AccommodationJpaEntity inactiveEntity = AccommodationJpaEntity.builder()
                .id(1L)
                .accommodationTypeId((short) 1)
                .name("Casa del Bosque")
                .pricePerNight(new BigDecimal("85.00"))
                .currency("EUR")
                .bedCapacity(4)
                .active(false)
                .build();

        when(repository.save(any(AccommodationJpaEntity.class))).thenReturn(inactiveEntity);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Accommodation result = adapter.save(inactiveAccommodation);

        assertThat(result.isActive()).isFalse();
        verify(repository).save(any(AccommodationJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle save error gracefully")
    void shouldHandleSaveErrorGracefully() {
        when(repository.save(any(AccommodationJpaEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> adapter.save(testAccommodation))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to save accommodation");

        verify(repository).save(any(AccommodationJpaEntity.class));
    }

    @Test
    @DisplayName("Should load accommodation by id successfully")
    void shouldLoadAccommodationByIdSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Optional<Accommodation> result = adapter.loadById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Casa del Bosque");
        assertThat(result.get().accommodationType().id()).isEqualTo((short) 1);

        verify(repository).findById(1L);
        verify(loadAccommodationTypePort).loadById((short) 1);
    }

    @Test
    @DisplayName("Should return empty when accommodation not found by id")
    void shouldReturnEmptyWhenAccommodationNotFoundById() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Accommodation> result = adapter.loadById(999L);

        assertThat(result).isEmpty();
        verify(repository).findById(999L);
        verify(loadAccommodationTypePort, never()).loadById(any());
    }

    @Test
    @DisplayName("Should throw exception when accommodation type not found during load")
    void shouldThrowExceptionWhenAccommodationTypeNotFoundDuringLoad() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.loadById(1L))
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("1");

        verify(repository).findById(1L);
        verify(loadAccommodationTypePort).loadById((short) 1);
    }

    @Test
    @DisplayName("Should find accommodation by name successfully")
    void shouldFindAccommodationByNameSuccessfully() {
        when(repository.findByName("Casa del Bosque")).thenReturn(Optional.of(testEntity));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Optional<Accommodation> result = adapter.findByName("Casa del Bosque");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Casa del Bosque");

        verify(repository).findByName("Casa del Bosque");
        verify(loadAccommodationTypePort).loadById((short) 1);
    }

    @Test
    @DisplayName("Should return empty when accommodation not found by name")
    void shouldReturnEmptyWhenAccommodationNotFoundByName() {
        when(repository.findByName("Inexistente")).thenReturn(Optional.empty());

        Optional<Accommodation> result = adapter.findByName("Inexistente");

        assertThat(result).isEmpty();
        verify(repository).findByName("Inexistente");
        verify(loadAccommodationTypePort, never()).loadById(any());
    }

    @Test
    @DisplayName("Should throw exception when accommodation type not found during find by name")
    void shouldThrowExceptionWhenAccommodationTypeNotFoundDuringFindByName() {
        when(repository.findByName("Casa del Bosque")).thenReturn(Optional.of(testEntity));
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.findByName("Casa del Bosque"))
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("1");

        verify(repository).findByName("Casa del Bosque");
        verify(loadAccommodationTypePort).loadById((short) 1);
    }

    @Test
    @DisplayName("Should find all accommodations successfully")
    void shouldFindAllAccommodationsSuccessfully() {
        AccommodationJpaEntity entity2 = AccommodationJpaEntity.builder()
                .id(2L)
                .accommodationTypeId((short) 1)
                .name("Villa del Mar")
                .pricePerNight(new BigDecimal("120.00"))
                .currency("EUR")
                .bedCapacity(6)
                .active(true)
                .build();

        AccommodationJpaEntity entity3 = AccommodationJpaEntity.builder()
                .id(3L)
                .accommodationTypeId((short) 1)
                .name("Apartamento Centro")
                .pricePerNight(new BigDecimal("60.00"))
                .currency("EUR")
                .bedCapacity(2)
                .active(false)
                .build();

        when(repository.findAll()).thenReturn(List.of(testEntity, entity2, entity3));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        List<Accommodation> result = adapter.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(2).id()).isEqualTo(3L);

        verify(repository).findAll();
        verify(loadAccommodationTypePort, times(3)).loadById((short) 1);
    }

    @Test
    @DisplayName("Should return empty list when no accommodations exist")
    void shouldReturnEmptyListWhenNoAccommodationsExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<Accommodation> result = adapter.findAll();

        assertThat(result).isEmpty();
        verify(repository).findAll();
        verify(loadAccommodationTypePort, never()).loadById(any());
    }

    @Test
    @DisplayName("Should find all active accommodations successfully")
    void shouldFindAllActiveAccommodationsSuccessfully() {
        AccommodationJpaEntity entity2 = AccommodationJpaEntity.builder()
                .id(2L)
                .accommodationTypeId((short) 1)
                .name("Villa del Mar")
                .pricePerNight(new BigDecimal("120.00"))
                .currency("EUR")
                .bedCapacity(6)
                .active(true)
                .build();

        when(repository.findAllByActiveTrue()).thenReturn(List.of(testEntity, entity2));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        List<Accommodation> result = adapter.findAllActive();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Accommodation::isActive);

        verify(repository).findAllByActiveTrue();
        verify(loadAccommodationTypePort, times(2)).loadById((short) 1);
    }

    @Test
    @DisplayName("Should return empty list when no active accommodations exist")
    void shouldReturnEmptyListWhenNoActiveAccommodationsExist() {
        when(repository.findAllByActiveTrue()).thenReturn(List.of());

        List<Accommodation> result = adapter.findAllActive();

        assertThat(result).isEmpty();
        verify(repository).findAllByActiveTrue();
        verify(loadAccommodationTypePort, never()).loadById(any());
    }

    @Test
    @DisplayName("Should find accommodations by accommodation type id successfully")
    void shouldFindAccommodationsByAccommodationTypeIdSuccessfully() {
        AccommodationJpaEntity entity2 = AccommodationJpaEntity.builder()
                .id(2L)
                .accommodationTypeId((short) 1)
                .name("Villa del Mar")
                .pricePerNight(new BigDecimal("120.00"))
                .currency("EUR")
                .bedCapacity(6)
                .active(true)
                .build();

        when(repository.findAllByAccommodationTypeId((short) 1))
                .thenReturn(List.of(testEntity, entity2));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        List<Accommodation> result = adapter.findByAccommodationTypeId((short) 1);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(acc -> acc.accommodationType().id().equals((short) 1));

        verify(repository).findAllByAccommodationTypeId((short) 1);
        verify(loadAccommodationTypePort, times(2)).loadById((short) 1);
    }

    @Test
    @DisplayName("Should return empty list when no accommodations of specific type exist")
    void shouldReturnEmptyListWhenNoAccommodationsOfSpecificTypeExist() {
        when(repository.findAllByAccommodationTypeId((short) 999)).thenReturn(List.of());

        List<Accommodation> result = adapter.findByAccommodationTypeId((short) 999);

        assertThat(result).isEmpty();
        verify(repository).findAllByAccommodationTypeId((short) 999);
        verify(loadAccommodationTypePort, never()).loadById(any());
    }

    @Test
    @DisplayName("Should throw exception when accommodation type not found during find all")
    void shouldThrowExceptionWhenAccommodationTypeNotFoundDuringFindAll() {
        when(repository.findAll()).thenReturn(List.of(testEntity));
        when(loadAccommodationTypePort.loadById((short) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.findAll())
                .isInstanceOf(AccommodationTypeNotFoundException.class)
                .hasMessageContaining("1");

        verify(repository).findAll();
        verify(loadAccommodationTypePort).loadById((short) 1);
    }

    @Test
    @DisplayName("Should preserve data integrity when saving")
    void shouldPreserveDataIntegrityWhenSaving() {
        Accommodation accommodation = new Accommodation(
                null,
                testAccommodationType,
                "Nueva Casa",
                Money.euros(new BigDecimal("150.75")),
                8,
                true
        );

        AccommodationJpaEntity savedEntity = AccommodationJpaEntity.builder()
                .id(5L)
                .accommodationTypeId((short) 1)
                .name("Nueva Casa")
                .pricePerNight(new BigDecimal("150.75"))
                .currency("EUR")
                .bedCapacity(8)
                .active(true)
                .build();

        when(repository.save(any(AccommodationJpaEntity.class))).thenReturn(savedEntity);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Accommodation result = adapter.save(accommodation);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.name()).isEqualTo("Nueva Casa");
        assertThat(result.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(result.bedCapacity()).isEqualTo(8);
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void shouldHandleSpecialCharactersInName() {
        Accommodation accommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Casa «El Olivo»",
                Money.euros(85.00),
                4,
                true
        );

        AccommodationJpaEntity entity = AccommodationJpaEntity.builder()
                .id(1L)
                .accommodationTypeId((short) 1)
                .name("Casa «El Olivo»")
                .pricePerNight(new BigDecimal("85.00"))
                .currency("EUR")
                .bedCapacity(4)
                .active(true)
                .build();

        when(repository.save(any(AccommodationJpaEntity.class))).thenReturn(entity);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Accommodation result = adapter.save(accommodation);

        assertThat(result.name()).isEqualTo("Casa «El Olivo»");
        verify(repository).save(any(AccommodationJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle very high prices")
    void shouldHandleVeryHighPrices() {
        Accommodation accommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Villa Presidencial",
                Money.euros(new BigDecimal("9999.99")),
                10,
                true
        );

        AccommodationJpaEntity entity = AccommodationJpaEntity.builder()
                .id(1L)
                .accommodationTypeId((short) 1)
                .name("Villa Presidencial")
                .pricePerNight(new BigDecimal("9999.99"))
                .currency("EUR")
                .bedCapacity(10)
                .active(true)
                .build();

        when(repository.save(any(AccommodationJpaEntity.class))).thenReturn(entity);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Accommodation result = adapter.save(accommodation);

        assertThat(result.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("9999.99"));
        verify(repository).save(any(AccommodationJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle minimum price")
    void shouldHandleMinimumPrice() {
        Accommodation accommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Habitación Básica",
                Money.euros(new BigDecimal("0.01")),
                1,
                true
        );

        AccommodationJpaEntity entity = AccommodationJpaEntity.builder()
                .id(1L)
                .accommodationTypeId((short) 1)
                .name("Habitación Básica")
                .pricePerNight(new BigDecimal("0.01"))
                .currency("EUR")
                .bedCapacity(1)
                .active(true)
                .build();

        when(repository.save(any(AccommodationJpaEntity.class))).thenReturn(entity);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Accommodation result = adapter.save(accommodation);

        assertThat(result.pricePerNight().value()).isEqualByComparingTo(new BigDecimal("0.01"));
        verify(repository).save(any(AccommodationJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle high bed capacity")
    void shouldHandleHighBedCapacity() {
        Accommodation accommodation = new Accommodation(
                1L,
                testAccommodationType,
                "Albergue Rural",
                Money.euros(200.00),
                20,
                true
        );

        AccommodationJpaEntity entity = AccommodationJpaEntity.builder()
                .id(1L)
                .accommodationTypeId((short) 1)
                .name("Albergue Rural")
                .pricePerNight(new BigDecimal("200.00"))
                .currency("EUR")
                .bedCapacity(20)
                .active(true)
                .build();

        when(repository.save(any(AccommodationJpaEntity.class))).thenReturn(entity);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Accommodation result = adapter.save(accommodation);

        assertThat(result.bedCapacity()).isEqualTo(20);
        verify(repository).save(any(AccommodationJpaEntity.class));
    }

    @Test
    @DisplayName("Should maintain list order when finding all")
    void shouldMaintainListOrderWhenFindingAll() {
        List<AccommodationJpaEntity> entities = List.of(
                AccommodationJpaEntity.builder()
                        .id(3L)
                        .accommodationTypeId((short) 1)
                        .name("C")
                        .pricePerNight(new BigDecimal("100.00"))
                        .currency("EUR")
                        .bedCapacity(6)
                        .active(true)
                        .build(),
                AccommodationJpaEntity.builder()
                        .id(1L)
                        .accommodationTypeId((short) 1)
                        .name("A")
                        .pricePerNight(new BigDecimal("50.00"))
                        .currency("EUR")
                        .bedCapacity(2)
                        .active(true)
                        .build(),
                AccommodationJpaEntity.builder()
                        .id(2L)
                        .accommodationTypeId((short) 1)
                        .name("B")
                        .pricePerNight(new BigDecimal("75.00"))
                        .currency("EUR")
                        .bedCapacity(4)
                        .active(true)
                        .build()
        );

        when(repository.findAll()).thenReturn(entities);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        List<Accommodation> result = adapter.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo(3L);
        assertThat(result.get(1).id()).isEqualTo(1L);
        assertThat(result.get(2).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should handle accommodations with different types in list")
    void shouldHandleAccommodationsWithDifferentTypesInList() {
        AccommodationType type2 = new AccommodationType((short) 2, "Apartamento", "Urbano");

        AccommodationJpaEntity entity1 = AccommodationJpaEntity.builder()
                .id(1L)
                .accommodationTypeId((short) 1)
                .name("Casa Rural")
                .pricePerNight(new BigDecimal("85.00"))
                .currency("EUR")
                .bedCapacity(4)
                .active(true)
                .build();

        AccommodationJpaEntity entity2 = AccommodationJpaEntity.builder()
                .id(2L)
                .accommodationTypeId((short) 2)
                .name("Apartamento")
                .pricePerNight(new BigDecimal("60.00"))
                .currency("EUR")
                .bedCapacity(2)
                .active(true)
                .build();

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));
        when(loadAccommodationTypePort.loadById((short) 2))
                .thenReturn(Optional.of(type2));

        List<Accommodation> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).accommodationType().id()).isEqualTo((short) 1);
        assertThat(result.get(1).accommodationType().id()).isEqualTo((short) 2);

        verify(loadAccommodationTypePort).loadById((short) 1);
        verify(loadAccommodationTypePort).loadById((short) 2);
    }

    @Test
    @DisplayName("Should preserve currency when saving and loading")
    void shouldPreserveCurrencyWhenSavingAndLoading() {
        when(repository.save(any(AccommodationJpaEntity.class))).thenReturn(testEntity);
        when(loadAccommodationTypePort.loadById((short) 1))
                .thenReturn(Optional.of(testAccommodationType));

        Accommodation result = adapter.save(testAccommodation);

        assertThat(result.pricePerNight().currency().getCurrencyCode()).isEqualTo("EUR");
        verify(repository).save(any(AccommodationJpaEntity.class));
    }
}