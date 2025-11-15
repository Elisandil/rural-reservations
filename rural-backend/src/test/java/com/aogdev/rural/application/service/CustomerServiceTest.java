package com.aogdev.rural.application.service;

import com.aogdev.rural.application.port.in.customer.CreateCustomerCommand;
import com.aogdev.rural.application.port.in.customer.UpdateCustomerCommand;
import com.aogdev.rural.application.port.out.customer.*;
import com.aogdev.rural.application.port.out.reservation.LoadReservationPort;
import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.exception.customer.CustomerAlreadyExistsException;
import com.aogdev.rural.domain.exception.customer.CustomerNotFoundException;
import com.aogdev.rural.domain.exception.reservation.ReservationNotFoundException;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.model.Reservation;
import com.aogdev.rural.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private SaveCustomerPort saveCustomerPort;

    @Mock
    private LoadCustomerPort loadCustomerPort;

    @Mock
    private FindCustomerByDniPort findCustomerByDniPort;

    @Mock
    private DeleteCustomerPort deleteCustomerPort;

    @Mock
    private ListCustomersPort listCustomersPort;

    @Mock
    private LoadReservationPort loadReservationPort;

    @InjectMocks
    private CustomerService customerService;

    private CreateCustomerCommand createCommand;
    private UpdateCustomerCommand updateCommand;
    private Customer customer;
    private Reservation reservation;
    private DNI dni;
    private PersonName name;
    private Phone phone;
    private Email email;

    @BeforeEach
    void setUp() {
        dni = new DNI("12345678Z");
        name = new PersonName("Juan", "García López");
        phone = new Phone("+34612345678");
        email = new Email("juan@example.com");

        createCommand = new CreateCustomerCommand(
                100L,
                name,
                phone,
                email,
                "España",
                Gender.MALE,
                false,
                dni
        );

        updateCommand = new UpdateCustomerCommand(
                1L,
                100L,
                name,
                phone,
                email,
                "España",
                Gender.MALE,
                false,
                dni
        );

        customer = new Customer(
                1L,
                100L,
                name,
                phone,
                email,
                "España",
                Gender.MALE,
                false,
                dni
        );

        reservation = new Reservation(
                100L,
                1L,
                1L,
                new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)),
                2,
                Money.euros(BigDecimal.valueOf(200)),
                false,
                LocalDate.now(),
                "Test reservation"
        );
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(findCustomerByDniPort.findByDni(dni)).thenReturn(Optional.empty());
        when(saveCustomerPort.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.create(createCommand);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.dni()).isEqualTo(dni);
        assertThat(result.name()).isEqualTo(name);

        verify(loadReservationPort).loadById(100L);
        verify(findCustomerByDniPort).findByDni(dni);
        verify(saveCustomerPort).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when creating customer with non-existent reservation")
    void shouldThrowExceptionWhenCreatingCustomerWithNonExistentReservation() {
        when(loadReservationPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.create(createCommand))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("100");

        verify(loadReservationPort).loadById(100L);
        verify(findCustomerByDniPort, never()).findByDni(any());
        verify(saveCustomerPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating customer with duplicate DNI")
    void shouldThrowExceptionWhenCreatingCustomerWithDuplicateDNI() {
        Customer existingCustomer = new Customer(
                2L,
                101L,
                new PersonName("María", "López"),
                phone,
                email,
                "España",
                Gender.FEMALE,
                false,
                dni
        );

        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(findCustomerByDniPort.findByDni(dni)).thenReturn(Optional.of(existingCustomer));

        assertThatThrownBy(() -> customerService.create(createCommand))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining(dni.value());

        verify(loadReservationPort).loadById(100L);
        verify(findCustomerByDniPort).findByDni(dni);
        verify(saveCustomerPort, never()).save(any());
    }

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() {
        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.dni()).isEqualTo(dni);

        verify(loadCustomerPort).loadById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent customer")
    void shouldThrowExceptionWhenGettingNonExistentCustomer() {
        when(loadCustomerPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getById(999L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadCustomerPort).loadById(999L);
    }

    @Test
    @DisplayName("Should find customer by DNI successfully")
    void shouldFindCustomerByDNISuccessfully() {
        when(findCustomerByDniPort.findByDni(dni)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.findByDni(dni);

        assertThat(result).isPresent();
        assertThat(result.get().dni()).isEqualTo(dni);

        verify(findCustomerByDniPort).findByDni(dni);
    }

    @Test
    @DisplayName("Should return empty when customer not found by DNI")
    void shouldReturnEmptyWhenCustomerNotFoundByDNI() {
        DNI nonExistentDni = new DNI("87654321A");
        when(findCustomerByDniPort.findByDni(nonExistentDni)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.findByDni(nonExistentDni);

        assertThat(result).isEmpty();

        verify(findCustomerByDniPort).findByDni(nonExistentDni);
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.of(customer));
        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(saveCustomerPort.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.update(updateCommand);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        verify(loadCustomerPort).loadById(1L);
        verify(loadReservationPort).loadById(100L);
        verify(saveCustomerPort).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent customer")
    void shouldThrowExceptionWhenUpdatingNonExistentCustomer() {
        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(updateCommand))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("1");

        verify(loadCustomerPort).loadById(1L);
        verify(loadReservationPort, never()).loadById(any());
        verify(saveCustomerPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating with non-existent reservation")
    void shouldThrowExceptionWhenUpdatingWithNonExistentReservation() {
        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.of(customer));
        when(loadReservationPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(updateCommand))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("100");

        verify(loadCustomerPort).loadById(1L);
        verify(loadReservationPort).loadById(100L);
        verify(saveCustomerPort, never()).save(any());
    }

    @Test
    @DisplayName("Should update customer with different DNI successfully")
    void shouldUpdateCustomerWithDifferentDNISuccessfully() {
        DNI newDni = new DNI("87654321A");
        UpdateCustomerCommand commandWithNewDni = new UpdateCustomerCommand(
                1L,
                100L,
                name,
                phone,
                email,
                "España",
                Gender.MALE,
                false,
                newDni
        );

        Customer updatedCustomer = new Customer(
                1L,
                100L,
                name,
                phone,
                email,
                "España",
                Gender.MALE,
                false,
                newDni
        );

        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.of(customer));
        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(findCustomerByDniPort.findByDni(newDni)).thenReturn(Optional.empty());
        when(saveCustomerPort.save(any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = customerService.update(commandWithNewDni);

        assertThat(result).isNotNull();
        assertThat(result.dni()).isEqualTo(newDni);

        verify(loadCustomerPort).loadById(1L);
        verify(loadReservationPort).loadById(100L);
        verify(findCustomerByDniPort).findByDni(newDni);
        verify(saveCustomerPort).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate DNI")
    void shouldThrowExceptionWhenUpdatingWithDuplicateDNI() {
        DNI newDni = new DNI("87654321A");
        Customer anotherCustomer = new Customer(
                2L,
                101L,
                new PersonName("María", "López"),
                phone,
                email,
                "España",
                Gender.FEMALE,
                false,
                newDni
        );

        UpdateCustomerCommand commandWithNewDni = new UpdateCustomerCommand(
                1L,
                100L,
                name,
                phone,
                email,
                "España",
                Gender.MALE,
                false,
                newDni
        );

        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.of(customer));
        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(findCustomerByDniPort.findByDni(newDni)).thenReturn(Optional.of(anotherCustomer));

        assertThatThrownBy(() -> customerService.update(commandWithNewDni))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining(newDni.value());

        verify(loadCustomerPort).loadById(1L);
        verify(loadReservationPort).loadById(100L);
        verify(findCustomerByDniPort).findByDni(newDni);
        verify(saveCustomerPort, never()).save(any());
    }

    @Test
    @DisplayName("Should allow updating same customer with same DNI")
    void shouldAllowUpdatingSameCustomerWithSameDNI() {
        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.of(customer));
        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(saveCustomerPort.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.update(updateCommand);

        assertThat(result).isNotNull();

        verify(loadCustomerPort).loadById(1L);
        verify(loadReservationPort).loadById(100L);
        verify(saveCustomerPort).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.of(customer));
        doNothing().when(deleteCustomerPort).deleteById(1L);

        customerService.delete(1L);

        verify(loadCustomerPort).loadById(1L);
        verify(deleteCustomerPort).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent customer")
    void shouldThrowExceptionWhenDeletingNonExistentCustomer() {
        when(loadCustomerPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.delete(999L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadCustomerPort).loadById(999L);
        verify(deleteCustomerPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should list all customers successfully")
    void shouldListAllCustomersSuccessfully() {
        Customer customer2 = new Customer(
                2L,
                101L,
                new PersonName("María", "López"),
                phone,
                email,
                "España",
                Gender.FEMALE,
                true,
                new DNI("87654321A")
        );

        List<Customer> customers = List.of(customer, customer2);
        when(listCustomersPort.findAll()).thenReturn(customers);

        List<Customer> result = customerService.listAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(customer, customer2);

        verify(listCustomersPort).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void shouldReturnEmptyListWhenNoCustomersExist() {
        when(listCustomersPort.findAll()).thenReturn(List.of());

        List<Customer> result = customerService.listAll();

        assertThat(result).isEmpty();

        verify(listCustomersPort).findAll();
    }

    @Test
    @DisplayName("Should list customers by reservation successfully")
    void shouldListCustomersByReservationSuccessfully() {
        Customer customer2 = new Customer(
                2L,
                100L,
                new PersonName("María", "López"),
                phone,
                email,
                "España",
                Gender.FEMALE,
                false,
                new DNI("87654321A")
        );

        List<Customer> customers = List.of(customer, customer2);
        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(listCustomersPort.findByReservationId(100L)).thenReturn(customers);

        List<Customer> result = customerService.listByReservation(100L);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(customer, customer2);

        verify(loadReservationPort).loadById(100L);
        verify(listCustomersPort).findByReservationId(100L);
    }

    @Test
    @DisplayName("Should throw exception when listing customers by non-existent reservation")
    void shouldThrowExceptionWhenListingCustomersByNonExistentReservation() {
        when(loadReservationPort.loadById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.listByReservation(999L))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("999");

        verify(loadReservationPort).loadById(999L);
        verify(listCustomersPort, never()).findByReservationId(any());
    }

    @Test
    @DisplayName("Should list pilgrims successfully")
    void shouldListPilgrimsSuccessfully() {
        Customer pilgrim1 = new Customer(
                1L,
                100L,
                new PersonName("Santiago", "Peregrino"),
                phone,
                email,
                "España",
                Gender.MALE,
                true,
                new DNI("12345678Z")
        );

        Customer pilgrim2 = new Customer(
                2L,
                101L,
                new PersonName("María", "Caminante"),
                phone,
                email,
                "Portugal",
                Gender.FEMALE,
                true,
                new DNI("87654321A")
        );

        List<Customer> pilgrims = List.of(pilgrim1, pilgrim2);
        when(listCustomersPort.findPilgrims()).thenReturn(pilgrims);

        List<Customer> result = customerService.listPilgrims();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Customer::isPilgrim);
        assertThat(result).containsExactly(pilgrim1, pilgrim2);

        verify(listCustomersPort).findPilgrims();
    }

    @Test
    @DisplayName("Should return empty list when no pilgrims exist")
    void shouldReturnEmptyListWhenNoPilgrimsExist() {
        when(listCustomersPort.findPilgrims()).thenReturn(List.of());

        List<Customer> result = customerService.listPilgrims();

        assertThat(result).isEmpty();

        verify(listCustomersPort).findPilgrims();
    }

    @Test
    @DisplayName("Should handle customer with null optional fields")
    void shouldHandleCustomerWithNullOptionalFields() {
        Customer customerWithNulls = new Customer(
                1L,
                100L,
                name,
                null,
                null,
                "España",
                Gender.MALE,
                false,
                dni
        );

        CreateCustomerCommand commandWithNulls = new CreateCustomerCommand(
                100L,
                name,
                null,
                null,
                "España",
                Gender.MALE,
                false,
                dni
        );

        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(findCustomerByDniPort.findByDni(dni)).thenReturn(Optional.empty());
        when(saveCustomerPort.save(any(Customer.class))).thenReturn(customerWithNulls);

        Customer result = customerService.create(commandWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.phone()).isNull();
        assertThat(result.email()).isNull();

        verify(saveCustomerPort).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should handle customer with isPilgrim true")
    void shouldHandleCustomerWithIsPilgrimTrue() {
        Customer pilgrim = new Customer(
                1L,
                100L,
                name,
                phone,
                email,
                "España",
                Gender.MALE,
                true,
                dni
        );

        CreateCustomerCommand pilgrimCommand = new CreateCustomerCommand(
                100L,
                name,
                phone,
                email,
                "España",
                Gender.MALE,
                true,
                dni
        );

        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(findCustomerByDniPort.findByDni(dni)).thenReturn(Optional.empty());
        when(saveCustomerPort.save(any(Customer.class))).thenReturn(pilgrim);

        Customer result = customerService.create(pilgrimCommand);

        assertThat(result).isNotNull();
        assertThat(result.isPilgrim()).isTrue();

        verify(saveCustomerPort).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should handle all gender types")
    void shouldHandleAllGenderTypes() {
        CreateCustomerCommand maleCommand = new CreateCustomerCommand(
                100L, name, phone, email, "España", Gender.MALE, false, new DNI("11111111H")
        );

        CreateCustomerCommand femaleCommand = new CreateCustomerCommand(
                100L, name, phone, email, "España", Gender.FEMALE, false, new DNI("22222222J")
        );

        CreateCustomerCommand otherCommand = new CreateCustomerCommand(
                100L, name, phone, email, "España", Gender.OTHER, false, new DNI("33333333P")
        );

        when(loadReservationPort.loadById(100L)).thenReturn(Optional.of(reservation));
        when(findCustomerByDniPort.findByDni(any(DNI.class))).thenReturn(Optional.empty());
        when(saveCustomerPort.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer male = customerService.create(maleCommand);
        Customer female = customerService.create(femaleCommand);
        Customer other = customerService.create(otherCommand);

        assertThat(male.gender()).isEqualTo(Gender.MALE);
        assertThat(female.gender()).isEqualTo(Gender.FEMALE);
        assertThat(other.gender()).isEqualTo(Gender.OTHER);

        verify(saveCustomerPort, times(3)).save(any(Customer.class));
    }
}