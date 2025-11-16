package com.aogdev.rural.infrastructure.adapter.in.rest.mapper;

import com.aogdev.rural.application.port.in.customer.CreateCustomerCommand;
import com.aogdev.rural.application.port.in.customer.UpdateCustomerCommand;
import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.CreateCustomerRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.CustomerResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.customer.UpdateCustomerRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CustomerRestMapper Tests")
class CustomerRestMapperTest {

    @Test
    @DisplayName("Should map CreateCustomerRequest to CreateCustomerCommand")
    void shouldMapCreateCustomerRequestToCommand() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                100L,
                "Juan",
                "García López",
                "+34612345678",
                "juan@example.com",
                "España",
                "M",
                false,
                "12345678Z"
        );

        CreateCustomerCommand command = CustomerRestMapper.toCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.reservationId()).isEqualTo(100L);
        assertThat(command.name()).isEqualTo(new PersonName("Juan", "García López"));
        assertThat(command.phone()).isEqualTo(new Phone("+34612345678"));
        assertThat(command.email()).isEqualTo(new Email("juan@example.com"));
        assertThat(command.nationality()).isEqualTo("España");
        assertThat(command.gender()).isEqualTo(Gender.MALE);
        assertThat(command.isPilgrim()).isFalse();
        assertThat(command.dni()).isEqualTo(new DNI("12345678Z"));
    }

    @Test
    @DisplayName("Should map CreateCustomerRequest with null email to Command")
    void shouldMapCreateCustomerRequestWithNullEmailToCommand() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                100L,
                "Juan",
                "García López",
                "+34612345678",
                null,
                "España",
                "M",
                false,
                "12345678Z"
        );

        CreateCustomerCommand command = CustomerRestMapper.toCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.email()).isNull();
        assertThat(command.phone()).isNotNull();
    }

    @Test
    @DisplayName("Should map CreateCustomerRequest with null isPilgrim to Command with default false")
    void shouldMapCreateCustomerRequestWithNullIsPilgrimToCommand() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                100L,
                "Juan",
                "García López",
                "+34612345678",
                "juan@example.com",
                "España",
                "M",
                null,
                "12345678Z"
        );

        CreateCustomerCommand command = CustomerRestMapper.toCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.isPilgrim()).isFalse();
    }

    @Test
    @DisplayName("Should map CreateCustomerRequest with isPilgrim true to Command")
    void shouldMapCreateCustomerRequestWithIsPilgrimTrueToCommand() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                100L,
                "Santiago",
                "Peregrino",
                "+34612345678",
                "santiago@example.com",
                "España",
                "M",
                true,
                "12345678Z"
        );

        CreateCustomerCommand command = CustomerRestMapper.toCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.isPilgrim()).isTrue();
    }

    @Test
    @DisplayName("Should map CreateCustomerRequest with female gender to Command")
    void shouldMapCreateCustomerRequestWithFemaleGenderToCommand() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                100L,
                "María",
                "López",
                "+34612345678",
                "maria@example.com",
                "España",
                "F",
                false,
                "87654321A"
        );

        CreateCustomerCommand command = CustomerRestMapper.toCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.gender()).isEqualTo(Gender.FEMALE);
    }

    @Test
    @DisplayName("Should map CreateCustomerRequest with other gender to Command")
    void shouldMapCreateCustomerRequestWithOtherGenderToCommand() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                100L,
                "Alex",
                "Pérez",
                "+34612345678",
                "alex@example.com",
                "España",
                "O",
                false,
                "11111111H"
        );

        CreateCustomerCommand command = CustomerRestMapper.toCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.gender()).isEqualTo(Gender.OTHER);
    }

    @Test
    @DisplayName("Should map CreateCustomerRequest with NIE to Command")
    void shouldMapCreateCustomerRequestWithNIEToCommand() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                100L,
                "John",
                "Smith",
                "+34612345678",
                "john@example.com",
                "Reino Unido",
                "M",
                false,
                "X1234567L"
        );

        CreateCustomerCommand command = CustomerRestMapper.toCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.dni()).isEqualTo(new DNI("X1234567L"));
        assertThat(command.nationality()).isEqualTo("Reino Unido");
    }

    @Test
    @DisplayName("Should map UpdateCustomerRequest to UpdateCustomerCommand")
    void shouldMapUpdateCustomerRequestToCommand() {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                101L,
                "María",
                "López García",
                "+34687654321",
                "maria@example.com",
                "Portugal",
                "F",
                true,
                "87654321A"
        );

        UpdateCustomerCommand command = CustomerRestMapper.toCommand(99L, request);

        assertThat(command).isNotNull();
        assertThat(command.id()).isEqualTo(99L);
        assertThat(command.reservationId()).isEqualTo(101L);
        assertThat(command.name()).isEqualTo(new PersonName("María", "López García"));
        assertThat(command.phone()).isEqualTo(new Phone("+34687654321"));
        assertThat(command.email()).isEqualTo(new Email("maria@example.com"));
        assertThat(command.nationality()).isEqualTo("Portugal");
        assertThat(command.gender()).isEqualTo(Gender.FEMALE);
        assertThat(command.isPilgrim()).isTrue();
        assertThat(command.dni()).isEqualTo(new DNI("87654321A"));
    }

    @Test
    @DisplayName("Should map UpdateCustomerRequest with null email to Command")
    void shouldMapUpdateCustomerRequestWithNullEmailToCommand() {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                100L,
                "Juan",
                "García",
                "+34612345678",
                null,
                "España",
                "M",
                false,
                "12345678Z"
        );

        UpdateCustomerCommand command = CustomerRestMapper.toCommand(1L, request);

        assertThat(command).isNotNull();
        assertThat(command.id()).isEqualTo(1L);
        assertThat(command.email()).isNull();
    }

    @Test
    @DisplayName("Should map UpdateCustomerRequest with null isPilgrim to Command with default false")
    void shouldMapUpdateCustomerRequestWithNullIsPilgrimToCommand() {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                100L,
                "Juan",
                "García",
                "+34612345678",
                "juan@example.com",
                "España",
                "M",
                null,
                "12345678Z"
        );

        UpdateCustomerCommand command = CustomerRestMapper.toCommand(1L, request);

        assertThat(command).isNotNull();
        assertThat(command.isPilgrim()).isFalse();
    }

    @Test
    @DisplayName("Should map Customer to CustomerResponse")
    void shouldMapCustomerToResponse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Juan", "García López"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.reservationId()).isEqualTo(100L);
        assertThat(response.firstName()).isEqualTo("Juan");
        assertThat(response.surnames()).isEqualTo("García López");
        assertThat(response.fullName()).isEqualTo("Juan García López");
        assertThat(response.phone()).isEqualTo("+34612345678");
        assertThat(response.phoneFormatted()).isEqualTo("+34 612 345 678");
        assertThat(response.email()).isEqualTo("juan@example.com");
        assertThat(response.nationality()).isEqualTo("España");
        assertThat(response.gender()).isEqualTo('M');
        assertThat(response.isPilgrim()).isFalse();
        assertThat(response.dni()).isEqualTo("12345678Z");
    }

    @Test
    @DisplayName("Should map Customer with null phone to Response")
    void shouldMapCustomerWithNullPhoneToResponse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                null,
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.phone()).isNull();
        assertThat(response.phoneFormatted()).isNull();
    }

    @Test
    @DisplayName("Should map Customer with null email to Response")
    void shouldMapCustomerWithNullEmailToResponse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                null,
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.email()).isNull();
    }

    @Test
    @DisplayName("Should map Customer with both phone and email null to Response")
    void shouldMapCustomerWithBothPhoneAndEmailNullToResponse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                null,
                null,
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.phone()).isNull();
        assertThat(response.phoneFormatted()).isNull();
        assertThat(response.email()).isNull();
    }

    @Test
    @DisplayName("Should map pilgrim Customer to Response")
    void shouldMapPilgrimCustomerToResponse() {
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

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.isPilgrim()).isTrue();
    }

    @Test
    @DisplayName("Should map female Customer to Response")
    void shouldMapFemaleCustomerToResponse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("María", "López"),
                new Phone("+34612345678"),
                new Email("maria@example.com"),
                "España",
                Gender.FEMALE,
                false,
                new DNI("87654321A")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.gender()).isEqualTo('F');
    }

    @Test
    @DisplayName("Should map Customer with other gender to Response")
    void shouldMapCustomerWithOtherGenderToResponse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Alex", "Pérez"),
                new Phone("+34612345678"),
                new Email("alex@example.com"),
                "España",
                Gender.OTHER,
                false,
                new DNI("11111111H")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.gender()).isEqualTo('O');
    }

    @Test
    @DisplayName("Should map Customer with NIE to Response")
    void shouldMapCustomerWithNIEToResponse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("John", "Smith"),
                new Phone("+34612345678"),
                new Email("john@example.com"),
                "Reino Unido",
                Gender.MALE,
                false,
                new DNI("X1234567L")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.dni()).isEqualTo("X1234567L");
        assertThat(response.nationality()).isEqualTo("Reino Unido");
    }

    @Test
    @DisplayName("Should map Customer with complex name to Response")
    void shouldMapCustomerWithComplexNameToResponse() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Ana María Isabel", "García López Fernández"),
                new Phone("+34612345678"),
                new Email("ana@example.com"),
                "España",
                Gender.FEMALE,
                false,
                new DNI("12345678Z")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo("Ana María Isabel");
        assertThat(response.surnames()).isEqualTo("García López Fernández");
        assertThat(response.fullName()).isEqualTo("Ana María Isabel García López");
    }

    @Test
    @DisplayName("Should map empty list of Customers to empty list of Responses")
    void shouldMapEmptyListOfCustomersToEmptyListOfResponses() {
        List<Customer> customers = List.of();

        List<CustomerResponse> responses = CustomerRestMapper.toResponseList(customers);

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Should map list of Customers to list of Responses")
    void shouldMapListOfCustomersToListOfResponses() {
        Customer customer1 = new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        );

        Customer customer2 = new Customer(
                2L,
                101L,
                new PersonName("María", "López"),
                new Phone("+34687654321"),
                new Email("maria@example.com"),
                "Portugal",
                Gender.FEMALE,
                true,
                new DNI("87654321A")
        );

        Customer customer3 = new Customer(
                3L,
                102L,
                new PersonName("Alex", "Pérez"),
                null,
                null,
                "Francia",
                Gender.OTHER,
                false,
                new DNI("11111111H")
        );

        List<Customer> customers = List.of(customer1, customer2, customer3);

        List<CustomerResponse> responses = CustomerRestMapper.toResponseList(customers);

        assertThat(responses).hasSize(3);

        assertThat(responses.getFirst().id()).isEqualTo(1L);
        assertThat(responses.get(0).firstName()).isEqualTo("Juan");
        assertThat(responses.get(0).dni()).isEqualTo("12345678Z");

        assertThat(responses.get(1).id()).isEqualTo(2L);
        assertThat(responses.get(1).firstName()).isEqualTo("María");
        assertThat(responses.get(1).isPilgrim()).isTrue();

        assertThat(responses.get(2).id()).isEqualTo(3L);
        assertThat(responses.get(2).phone()).isNull();
        assertThat(responses.get(2).email()).isNull();
        assertThat(responses.get(2).gender()).isEqualTo('O');
    }

    @Test
    @DisplayName("Should preserve order when mapping list of Customers")
    void shouldPreserveOrderWhenMappingListOfCustomers() {
        Customer customer1 = new Customer(
                3L, 100L, new PersonName("Charlie", "Brown"),
                new Phone("+34612345678"), new Email("charlie@example.com"),
                "España", Gender.MALE, false, new DNI("33333333P")
        );

        Customer customer2 = new Customer(
                1L, 100L, new PersonName("Alice", "Smith"),
                new Phone("+34612345678"), new Email("alice@example.com"),
                "España", Gender.FEMALE, false, new DNI("11111111H")
        );

        Customer customer3 = new Customer(
                2L, 100L, new PersonName("Bob", "Jones"),
                new Phone("+34612345678"), new Email("bob@example.com"),
                "España", Gender.MALE, false, new DNI("22222222J")
        );

        List<Customer> customers = List.of(customer1, customer2, customer3);

        List<CustomerResponse> responses = CustomerRestMapper.toResponseList(customers);

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).id()).isEqualTo(3L);
        assertThat(responses.get(1).id()).isEqualTo(1L);
        assertThat(responses.get(2).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should handle Spanish phone formatting correctly")
    void shouldHandleSpanishPhoneFormattingCorrectly() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("Juan", "García"),
                new Phone("+34612345678"),
                new Email("juan@example.com"),
                "España",
                Gender.MALE,
                false,
                new DNI("12345678Z")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response.phone()).isEqualTo("+34612345678");
        assertThat(response.phoneFormatted()).isEqualTo("+34 612 345 678");
    }

    @Test
    @DisplayName("Should handle non-Spanish phone formatting")
    void shouldHandleNonSpanishPhoneFormatting() {
        Customer customer = new Customer(
                1L,
                100L,
                new PersonName("John", "Smith"),
                new Phone("+441234567890"),
                new Email("john@example.com"),
                "Reino Unido",
                Gender.MALE,
                false,
                new DNI("X1234567L")
        );

        CustomerResponse response = CustomerRestMapper.toResponse(customer);

        assertThat(response.phone()).isEqualTo("+441234567890");
        assertThat(response.phoneFormatted()).isEqualTo("+441234567890");
    }

    @Test
    @DisplayName("Should map all gender types correctly in requests")
    void shouldMapAllGenderTypesCorrectlyInRequests() {
        CreateCustomerRequest maleRequest = new CreateCustomerRequest(
                100L, "Juan", "García", "+34612345678", "juan@example.com",
                "España", "M", false, "11111111H"
        );

        CreateCustomerRequest femaleRequest = new CreateCustomerRequest(
                100L, "María", "López", "+34612345678", "maria@example.com",
                "España", "F", false, "22222222J"
        );

        CreateCustomerRequest otherRequest = new CreateCustomerRequest(
                100L, "Alex", "Pérez", "+34612345678", "alex@example.com",
                "España", "O", false, "33333333P"
        );

        CreateCustomerCommand maleCommand = CustomerRestMapper.toCommand(maleRequest);
        CreateCustomerCommand femaleCommand = CustomerRestMapper.toCommand(femaleRequest);
        CreateCustomerCommand otherCommand = CustomerRestMapper.toCommand(otherRequest);

        assertThat(maleCommand.gender()).isEqualTo(Gender.MALE);
        assertThat(femaleCommand.gender()).isEqualTo(Gender.FEMALE);
        assertThat(otherCommand.gender()).isEqualTo(Gender.OTHER);
    }

    @Test
    @DisplayName("Should map all gender types correctly in responses")
    void shouldMapAllGenderTypesCorrectlyInResponses() {
        Customer maleCustomer = new Customer(
                1L, 100L, new PersonName("Juan", "García"),
                new Phone("+34612345678"), new Email("juan@example.com"),
                "España", Gender.MALE, false, new DNI("11111111H")
        );

        Customer femaleCustomer = new Customer(
                2L, 100L, new PersonName("María", "López"),
                new Phone("+34612345678"), new Email("maria@example.com"),
                "España", Gender.FEMALE, false, new DNI("22222222J")
        );

        Customer otherCustomer = new Customer(
                3L, 100L, new PersonName("Alex", "Pérez"),
                new Phone("+34612345678"), new Email("alex@example.com"),
                "España", Gender.OTHER, false, new DNI("33333333P")
        );

        CustomerResponse maleResponse = CustomerRestMapper.toResponse(maleCustomer);
        CustomerResponse femaleResponse = CustomerRestMapper.toResponse(femaleCustomer);
        CustomerResponse otherResponse = CustomerRestMapper.toResponse(otherCustomer);

        assertThat(maleResponse.gender()).isEqualTo('M');
        assertThat(femaleResponse.gender()).isEqualTo('F');
        assertThat(otherResponse.gender()).isEqualTo('O');
    }
}