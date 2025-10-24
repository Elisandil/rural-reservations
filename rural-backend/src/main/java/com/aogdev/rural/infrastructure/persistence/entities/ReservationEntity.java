package com.aogdev.rural.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservations_accommodation_id", columnList = "accommodation_id, start_date, end_date")
})
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private AccommodationEntity accommodation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private AdminEntity admin;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "beds_reserved", nullable = false)
    @Builder.Default
    private Integer bedsReserved = 1;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    @Builder.Default
    private Boolean paid = false;

    @CreationTimestamp
    @Column(name = "booking_date", nullable = false, updatable = false)
    private LocalDate bookingDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CustomerEntity> customers = new HashSet<>();

    @PrePersist
    @PreUpdate
    private void validateDates() {

        if (endDate != null && startDate != null && !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("The end date must be after the start date");
        }

        if (bedsReserved != null && bedsReserved <= 0) {
            throw new IllegalArgumentException("The number of beds reserved must be greater than 0");
        }
    }
}
