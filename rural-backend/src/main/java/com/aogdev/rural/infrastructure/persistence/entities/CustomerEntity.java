package com.aogdev.rural.infrastructure.entity.entities;

import com.aogdev.rural.infrastructure.entity.converters.GenderConverter;
import com.aogdev.rural.infrastructure.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customers_name", columnList = "reservation_id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surnames;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false, length = 1)
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Column(name = "is_pilgrim", nullable = false)
    @Builder.Default
    private Boolean isPilgrim = false;

    @Column(name = "dni", nullable = false, length = 20)
    private String dni;
}
