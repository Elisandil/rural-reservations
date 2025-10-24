package com.aogdev.rural.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accommodation_types")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccommodationTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /*
    @OneToMany(mappedBy = "accommodationType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AccommodationEntity> accommodations = new HashSet<>();
    */
}

