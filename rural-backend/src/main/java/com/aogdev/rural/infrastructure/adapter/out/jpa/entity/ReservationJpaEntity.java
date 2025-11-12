package com.aogdev.rural.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accommodationId;

    @Column(nullable = false)
    private Long adminId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer bedsReserved;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false, updatable = false)
    private LocalDate bookingDate;

    @Column(length = 1000)
    private String notes;

    @PrePersist
    protected void onCreate() {

        if (bookingDate == null) {
            bookingDate = LocalDate.now();
        }
    }
}