package com.aogdev.rural.infrastructure.config;

import com.aogdev.rural.application.port.out.accommodation.LoadAccommodationPort;
import com.aogdev.rural.application.port.out.admin.LoadAdminPort;
import com.aogdev.rural.application.port.out.reservation.*;
import com.aogdev.rural.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ReservationConfiguration {

    @Bean
    public ReservationService reservationService(
            SaveReservationPort saveReservationPort,
            LoadReservationPort loadReservationPort,
            DeleteReservationPort deleteReservationPort,
            ListReservationsPort listReservationsPort,
            FindOverlappingReservationsPort findOverlappingReservationsPort,
            LoadAccommodationPort loadAccommodationPort,
            LoadAdminPort loadAdminPort) {

        return new ReservationService(
                saveReservationPort,
                loadReservationPort,
                deleteReservationPort,
                listReservationsPort,
                findOverlappingReservationsPort,
                loadAccommodationPort,
                loadAdminPort
        );
    }
}