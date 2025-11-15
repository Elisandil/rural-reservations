package com.aogdev.rural.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 200)
    private String surnames;

    @Column(length = 15)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String nationality;

    @Column(nullable = false, length = 1)
    private Character gender;

    @Column(nullable = false)
    private Boolean isPilgrim;

    @Column(nullable = false, unique = true, length = 10)
    private String dni;
}