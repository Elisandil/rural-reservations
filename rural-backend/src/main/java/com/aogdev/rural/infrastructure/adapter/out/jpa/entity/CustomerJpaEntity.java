package com.aogdev.rural.infrastructure.adapter.out.jpa.entity;

import com.aogdev.rural.domain.enumerated.Gender;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.converter.GenderAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customers_reservation", columnList = "reservation_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 200)
    private String surnames;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String nationality;

    @Column(nullable = false, length = 1)
    @Convert(converter = GenderAttributeConverter.class)
    private Gender gender;

    @Column(name = "is_pilgrim", nullable = false)
    @Builder.Default
    private Boolean isPilgrim = false;

    @Column(name = "dni", nullable = false, length = 20)
    private String dni;
}