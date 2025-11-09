package com.aogdev.rural.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservations_accommodation", columnList = "accommodation_id"),
        @Index(name = "idx_reservations_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_reservations_admin", columnList = "admin_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "beds_reserved", nullable = false)
    private Integer bedsReserved;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    @Builder.Default
    private Boolean paid = false;

    @Column(name = "booking_date", nullable = false, updatable = false)
    private LocalDate bookingDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {

        if (bookingDate == null) {
            bookingDate = LocalDate.now();
        }
        validateDates();
    }

    @PreUpdate
    protected void onUpdate() {
        validateDates();
    }
    
    private void validateDates() {

        if (endDate != null && startDate != null && !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (bedsReserved != null && bedsReserved <= 0) {
            throw new IllegalArgumentException("Beds reserved must be greater than 0");
        }
    }
}