package com.aogdev.rural.infrastructure.adapter.out.jpa;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.CustomerJpaEntity;
import com.aogdev.rural.infrastructure.adapter.out.jpa.repository.CustomerJpaRepository;
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
@DisplayName("CustomerPersistenceAdapter Tests")
class CustomerPersistenceAdapterTest {

    @Mock
    private CustomerJpaRepository repository;

    @InjectMocks
    private CustomerPersistenceAdapter adapter;

    private Customer customer;
    private CustomerJpaEntity customerEntity;
    private DNI dni;

    @BeforeEach
    void setUp() {
        dni = new DNI("12345678Z");

        customer = new Customer(
                1L,
                100L,
                new PersonName("Juan", "García López"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                dni
        );

        customerEntity = CustomerJpaEntity.builder()
                .id(1L)
                .reservationId(100L)
                .firstName("Juan")
                .surnames("García López")
                .phone("+34612345678")
                .email("juan@example.com")
                .nationality("España")
                .gender('M')
                .isPilgrim(false)
                .dni("12345678Z")
                .build();
    }

    @Test
    @DisplayName("Should save customer successfully")
    void shouldSaveCustomerSuccessfully() {
        when(repository.save(any(CustomerJpaEntity.class))).thenReturn(customerEntity);

        Customer result = adapter.save(customer);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.dni().value()).isEqualTo("12345678Z");
        assertThat(result.name().firstName()).isEqualTo("Juan");
        assertThat(result.reservationId()).isEqualTo(100L);

        verify(repository).save(any(CustomerJpaEntity.class));
    }

    @Test
    @DisplayName("Should save customer with null optional fields")
    void shouldSaveCustomerWithNullOptionalFields() {
        Customer customerWithNulls = new Customer(
                null,
                100L,
                new PersonName("María", "López"),
                null,
                null,
                "España",
                Gender.FEMALE,
                false,
                new DNI("87654321A")
        );

        CustomerJpaEntity entityWithNulls = CustomerJpaEntity.builder()
                .id(2L)
                .reservationId(100L)
                .firstName("María")
                .surnames("López")
                .phone(null)
                .email(null)
                .nationality("España")
                .gender('F')
                .isPilgrim(false)
                .dni("87654321A")
                .build();

        when(repository.save(any(CustomerJpaEntity.class))).thenReturn(entityWithNulls);

        Customer result = adapter.save(customerWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.phone()).isNull();
        assertThat(result.email()).isNull();

        verify(repository).save(any(CustomerJpaEntity.class));
    }

    @Test
    @DisplayName("Should save pilgrim customer")
    void shouldSavePilgrimCustomer() {
        Customer pilgrim = new Customer(
                null,
                100L,
                new PersonName("Santiago", "Peregrino"),
                new Phone("+34612345678"),
                new Email("santiago@example.com"),
                "España",
                Gender.MALE,
                true,
                new DNI("11111111H")
        );

        CustomerJpaEntity pilgrimEntity = CustomerJpaEntity.builder()
                .id(3L)
                .reservationId(100L)
                .firstName("Santiago")
                .surnames("Peregrino")
                .phone("+34612345678")
                .email("santiago@example.com")
                .nationality("España")
                .gender('M')
                .isPilgrim(true)
                .dni("11111111H")
                .build();

        when(repository.save(any(CustomerJpaEntity.class))).thenReturn(pilgrimEntity);

        Customer result = adapter.save(pilgrim);

        assertThat(result).isNotNull();
        assertThat(result.isPilgrim()).isTrue();

        verify(repository).save(any(CustomerJpaEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when save fails")
    void shouldThrowExceptionWhenSaveFails() {
        when(repository.save(any(CustomerJpaEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> adapter.save(customer))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to save customer");

        verify(repository).save(any(CustomerJpaEntity.class));
    }

    @Test
    @DisplayName("Should load customer by id successfully")
    void shouldLoadCustomerByIdSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(customerEntity));

        Optional<Customer> result = adapter.loadById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().dni().value()).isEqualTo("12345678Z");

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when customer not found by id")
    void shouldReturnEmptyWhenCustomerNotFoundById() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Customer> result = adapter.loadById(999L);

        assertThat(result).isEmpty();

        verify(repository).findById(999L);
    }

    @Test
    @DisplayName("Should find customer by DNI successfully")
    void shouldFindCustomerByDNISuccessfully() {
        when(repository.findByDni("12345678Z")).thenReturn(Optional.of(customerEntity));

        Optional<Customer> result = adapter.findByDni(dni);

        assertThat(result).isPresent();
        assertThat(result.get().dni()).isEqualTo(dni);
        assertThat(result.get().name().firstName()).isEqualTo("Juan");

        verify(repository).findByDni("12345678Z");
    }

    @Test
    @DisplayName("Should return empty when customer not found by DNI")
    void shouldReturnEmptyWhenCustomerNotFoundByDNI() {
        DNI nonExistentDni = new DNI("99999999R");
        when(repository.findByDni("99999999R")).thenReturn(Optional.empty());

        Optional<Customer> result = adapter.findByDni(nonExistentDni);

        assertThat(result).isEmpty();

        verify(repository).findByDni("99999999R");
    }

    @Test
    @DisplayName("Should find customer by NIE successfully")
    void shouldFindCustomerByNIESuccessfully() {
        DNI nie = new DNI("X1234567L");
        CustomerJpaEntity nieEntity = CustomerJpaEntity.builder()
                .id(1L)
                .reservationId(100L)
                .firstName("John")
                .surnames("Smith")
                .phone("+34612345678")
                .email("john@example.com")
                .nationality("Reino Unido")
                .gender('M')
                .isPilgrim(false)
                .dni("X1234567L")
                .build();

        when(repository.findByDni("X1234567L")).thenReturn(Optional.of(nieEntity));

        Optional<Customer> result = adapter.findByDni(nie);

        assertThat(result).isPresent();
        assertThat(result.get().dni().value()).isEqualTo("X1234567L");

        verify(repository).findByDni("X1234567L");
    }

    @Test
    @DisplayName("Should delete customer by id successfully")
    void shouldDeleteCustomerByIdSuccessfully() {
        doNothing().when(repository).deleteById(1L);

        adapter.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when delete fails")
    void shouldThrowExceptionWhenDeleteFails() {
        doThrow(new RuntimeException("Database error"))
                .when(repository).deleteById(1L);

        assertThatThrownBy(() -> adapter.deleteById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to delete customer");

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Should find all customers successfully")
    void shouldFindAllCustomersSuccessfully() {
        CustomerJpaEntity entity2 = CustomerJpaEntity.builder()
                .id(2L)
                .reservationId(101L)
                .firstName("María")
                .surnames("López")
                .phone("+34687654321")
                .email("maria@example.com")
                .nationality("España")
                .gender('F')
                .isPilgrim(true)
                .dni("87654321A")
                .build();

        List<CustomerJpaEntity> entities = List.of(customerEntity, entity2);
        when(repository.findAll()).thenReturn(entities);

        List<Customer> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).isPilgrim()).isTrue();

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void shouldReturnEmptyListWhenNoCustomersExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<Customer> result = adapter.findAll();

        assertThat(result).isEmpty();

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should find customers by reservation id successfully")
    void shouldFindCustomersByReservationIdSuccessfully() {
        CustomerJpaEntity entity2 = CustomerJpaEntity.builder()
                .id(2L)
                .reservationId(100L)
                .firstName("María")
                .surnames("López")
                .phone("+34687654321")
                .email("maria@example.com")
                .nationality("España")
                .gender('F')
                .isPilgrim(false)
                .dni("87654321A")
                .build();

        List<CustomerJpaEntity> entities = List.of(customerEntity, entity2);
        when(repository.findByReservationId(100L)).thenReturn(entities);

        List<Customer> result = adapter.findByReservationId(100L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.reservationId().equals(100L));
        assertThat(result.get(0).dni().value()).isEqualTo("12345678Z");
        assertThat(result.get(1).dni().value()).isEqualTo("87654321A");

        verify(repository).findByReservationId(100L);
    }

    @Test
    @DisplayName("Should return empty list when no customers for reservation")
    void shouldReturnEmptyListWhenNoCustomersForReservation() {
        when(repository.findByReservationId(999L)).thenReturn(List.of());

        List<Customer> result = adapter.findByReservationId(999L);

        assertThat(result).isEmpty();

        verify(repository).findByReservationId(999L);
    }

    @Test
    @DisplayName("Should find pilgrims successfully")
    void shouldFindPilgrimsSuccessfully() {
        CustomerJpaEntity pilgrim1 = CustomerJpaEntity.builder()
                .id(1L)
                .reservationId(100L)
                .firstName("Santiago")
                .surnames("Peregrino")
                .phone("+34612345678")
                .email("santiago@example.com")
                .nationality("España")
                .gender('M')
                .isPilgrim(true)
                .dni("11111111H")
                .build();

        CustomerJpaEntity pilgrim2 = CustomerJpaEntity.builder()
                .id(2L)
                .reservationId(101L)
                .firstName("María")
                .surnames("Caminante")
                .phone("+34687654321")
                .email("maria@example.com")
                .nationality("Portugal")
                .gender('F')
                .isPilgrim(true)
                .dni("22222222J")
                .build();

        List<CustomerJpaEntity> pilgrims = List.of(pilgrim1, pilgrim2);
        when(repository.findByIsPilgrimTrue()).thenReturn(pilgrims);

        List<Customer> result = adapter.findPilgrims();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Customer::isPilgrim);
        assertThat(result.get(0).name().firstName()).isEqualTo("Santiago");
        assertThat(result.get(1).name().firstName()).isEqualTo("María");

        verify(repository).findByIsPilgrimTrue();
    }

    @Test
    @DisplayName("Should return empty list when no pilgrims exist")
    void shouldReturnEmptyListWhenNoPilgrimsExist() {
        when(repository.findByIsPilgrimTrue()).thenReturn(List.of());

        List<Customer> result = adapter.findPilgrims();

        assertThat(result).isEmpty();

        verify(repository).findByIsPilgrimTrue();
    }

    @Test
    @DisplayName("Should handle all gender types correctly")
    void shouldHandleAllGenderTypesCorrectly() {
        CustomerJpaEntity maleEntity = CustomerJpaEntity.builder()
                .id(1L).reservationId(100L).firstName("Juan").surnames("García")
                .phone("+34612345678").email("juan@example.com").nationality("España")
                .gender('M').isPilgrim(false).dni("11111111H")
                .build();

        CustomerJpaEntity femaleEntity = CustomerJpaEntity.builder()
                .id(2L).reservationId(100L).firstName("María").surnames("López")
                .phone("+34612345678").email("maria@example.com").nationality("España")
                .gender('F').isPilgrim(false).dni("22222222J")
                .build();

        CustomerJpaEntity otherEntity = CustomerJpaEntity.builder()
                .id(3L).reservationId(100L).firstName("Alex").surnames("Pérez")
                .phone("+34612345678").email("alex@example.com").nationality("España")
                .gender('O').isPilgrim(false).dni("33333333P")
                .build();

        when(repository.findAll()).thenReturn(List.of(maleEntity, femaleEntity, otherEntity));

        List<Customer> result = adapter.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).gender()).isEqualTo(Gender.MALE);
        assertThat(result.get(1).gender()).isEqualTo(Gender.FEMALE);
        assertThat(result.get(2).gender()).isEqualTo(Gender.OTHER);

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should handle customer with null phone and email")
    void shouldHandleCustomerWithNullPhoneAndEmail() {
        CustomerJpaEntity entityWithNulls = CustomerJpaEntity.builder()
                .id(1L)
                .reservationId(100L)
                .firstName("Juan")
                .surnames("García")
                .phone(null)
                .email(null)
                .nationality("España")
                .gender('M')
                .isPilgrim(false)
                .dni("12345678Z")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entityWithNulls));

        Optional<Customer> result = adapter.loadById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().phone()).isNull();
        assertThat(result.get().email()).isNull();

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should preserve order when finding all customers")
    void shouldPreserveOrderWhenFindingAllCustomers() {
        CustomerJpaEntity entity1 = CustomerJpaEntity.builder()
                .id(3L).reservationId(100L).firstName("Charlie").surnames("Brown")
                .phone("+34612345678").email("charlie@example.com").nationality("España")
                .gender('M').isPilgrim(false).dni("33333333P")
                .build();

        CustomerJpaEntity entity2 = CustomerJpaEntity.builder()
                .id(1L).reservationId(100L).firstName("Alice").surnames("Smith")
                .phone("+34612345678").email("alice@example.com").nationality("España")
                .gender('F').isPilgrim(false).dni("11111111H")
                .build();

        CustomerJpaEntity entity3 = CustomerJpaEntity.builder()
                .id(2L).reservationId(100L).firstName("Bob").surnames("Jones")
                .phone("+34612345678").email("bob@example.com").nationality("España")
                .gender('M').isPilgrim(false).dni("22222222J")
                .build();

        when(repository.findAll()).thenReturn(List.of(entity1, entity2, entity3));

        List<Customer> result = adapter.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo(3L);
        assertThat(result.get(1).id()).isEqualTo(1L);
        assertThat(result.get(2).id()).isEqualTo(2L);

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should handle multiple nationalities correctly")
    void shouldHandleMultipleNationalitiesCorrectly() {
        CustomerJpaEntity spanish = CustomerJpaEntity.builder()
                .id(1L).reservationId(100L).firstName("Juan").surnames("García")
                .phone("+34612345678").email("juan@example.com").nationality("España")
                .gender('M').isPilgrim(false).dni("12345678Z")
                .build();

        CustomerJpaEntity portuguese = CustomerJpaEntity.builder()
                .id(2L).reservationId(100L).firstName("João").surnames("Silva")
                .phone("+34612345678").email("joao@example.com").nationality("Portugal")
                .gender('M').isPilgrim(false).dni("87654321A")
                .build();

        CustomerJpaEntity british = CustomerJpaEntity.builder()
                .id(3L).reservationId(100L).firstName("John").surnames("Smith")
                .phone("+34612345678").email("john@example.com").nationality("Reino Unido")
                .gender('M').isPilgrim(false).dni("X1234567L")
                .build();

        when(repository.findAll()).thenReturn(List.of(spanish, portuguese, british));

        List<Customer> result = adapter.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).nationality()).isEqualTo("España");
        assertThat(result.get(1).nationality()).isEqualTo("Portugal");
        assertThat(result.get(2).nationality()).isEqualTo("Reino Unido");

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should handle complex names correctly")
    void shouldHandleComplexNamesCorrectly() {
        CustomerJpaEntity complexName = CustomerJpaEntity.builder()
                .id(1L)
                .reservationId(100L)
                .firstName("Ana María Isabel")
                .surnames("García López Fernández")
                .phone("+34612345678")
                .email("ana@example.com")
                .nationality("España")
                .gender('F')
                .isPilgrim(false)
                .dni("12345678Z")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(complexName));

        Optional<Customer> result = adapter.loadById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().name().firstName()).isEqualTo("Ana María Isabel");
        assertThat(result.get().name().surnames()).isEqualTo("García López Fernández");

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should correctly map entity to domain and back")
    void shouldCorrectlyMapEntityToDomainAndBack() {
        when(repository.save(any(CustomerJpaEntity.class))).thenReturn(customerEntity);
        when(repository.findById(1L)).thenReturn(Optional.of(customerEntity));

        Customer saved = adapter.save(customer);
        Optional<Customer> loaded = adapter.loadById(1L);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().id()).isEqualTo(saved.id());
        assertThat(loaded.get().dni()).isEqualTo(saved.dni());
        assertThat(loaded.get().name().firstName()).isEqualTo(saved.name().firstName());
        assertThat(loaded.get().nationality()).isEqualTo(saved.nationality());

        verify(repository).save(any(CustomerJpaEntity.class));
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should handle update scenario correctly")
    void shouldHandleUpdateScenarioCorrectly() {
        Customer updatedCustomer = new Customer(
                1L,
                100L,
                new PersonName("Juan Carlos", "García López"),
                new Phone("+34687654321"),
                new Email("juancarlos@example.com"),
                "España",
                Gender.MALE,
                true,
                dni
        );

        CustomerJpaEntity updatedEntity = CustomerJpaEntity.builder()
                .id(1L)
                .reservationId(100L)
                .firstName("Juan Carlos")
                .surnames("García López")
                .phone("+34687654321")
                .email("juancarlos@example.com")
                .nationality("España")
                .gender('M')
                .isPilgrim(true)
                .dni("12345678Z")
                .build();

        when(repository.save(any(CustomerJpaEntity.class))).thenReturn(updatedEntity);

        Customer result = adapter.save(updatedCustomer);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name().firstName()).isEqualTo("Juan Carlos");
        assertThat(result.phone().value()).isEqualTo("+34687654321");
        assertThat(result.isPilgrim()).isTrue();

        verify(repository).save(any(CustomerJpaEntity.class));
    }

    @Test
    @DisplayName("Should handle filtering pilgrims from mixed list")
    void shouldHandleFilteringPilgrimsFromMixedList() {
        CustomerJpaEntity pilgrim = CustomerJpaEntity.builder()
                .id(1L).reservationId(100L).firstName("Santiago").surnames("Peregrino")
                .phone("+34612345678").email("santiago@example.com").nationality("España")
                .gender('M').isPilgrim(true).dni("11111111H")
                .build();

        when(repository.findByIsPilgrimTrue()).thenReturn(List.of(pilgrim));

        List<Customer> result = adapter.findPilgrims();

        assertThat(result).hasSize(1);
        assertThat(result).allMatch(Customer::isPilgrim);

        verify(repository).findByIsPilgrimTrue();
    }

    @Test
    @DisplayName("Should handle DNI normalization correctly")
    void shouldHandleDNINormalizationCorrectly() {
        DNI normalizedDni = new DNI("  12345678z  ");

        CustomerJpaEntity entity = CustomerJpaEntity.builder()
                .id(1L).reservationId(100L).firstName("Juan").surnames("García")
                .phone("+34612345678").email("juan@example.com").nationality("España")
                .gender('M').isPilgrim(false).dni("12345678Z")
                .build();

        when(repository.findByDni("12345678Z")).thenReturn(Optional.of(entity));

        Optional<Customer> result = adapter.findByDni(normalizedDni);

        assertThat(result).isPresent();
        assertThat(result.get().dni().value()).isEqualTo("12345678Z");

        verify(repository).findByDni("12345678Z");
    }
}