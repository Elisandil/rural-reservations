package com.aogdev.rural.infrastructure.adapter.out.jpa.mapper;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.domain.model.Customer;
import com.aogdev.rural.domain.valueobject.DNI;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.CustomerJpaEntity;

public class CustomerMapper {

    public static Customer toDomain(CustomerJpaEntity entity) {

        if (entity == null) {
            return null;
        }

        return new Customer(
                entity.getId(),
                entity.getReservationId(),
                new PersonName(entity.getFirstName(), entity.getSurnames()),
                entity.getPhone() != null ? new Phone(entity.getPhone()) : null,
                entity.getEmail() != null ? new Email(entity.getEmail()) : null,
                entity.getNationality(),
                Gender.fromCode(entity.getGender()),
                entity.getIsPilgrim(),
                new DNI(entity.getDni())
        );
    }

    public static CustomerJpaEntity toEntity(Customer customer) {

        if (customer == null) {
            return null;
        }

        return CustomerJpaEntity.builder()
                .id(customer.id())
                .reservationId(customer.reservationId())
                .firstName(customer.name().firstName())
                .surnames(customer.name().surnames())
                .phone(customer.phone() != null ? customer.phone().value() : null)
                .email(customer.email() != null ? customer.email().value() : null)
                .nationality(customer.nationality())
                .gender(customer.gender().getCode())
                .isPilgrim(customer.isPilgrim())
                .dni(customer.dni().value())
                .build();
    }
}