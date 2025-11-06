package com.aogdev.rural.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accommodation_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationTypeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;
}