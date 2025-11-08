package com.aogdev.rural.infrastructure.config;

import com.aogdev.rural.application.port.out.accommodation.*;
import com.aogdev.rural.application.port.out.accommodationType.LoadAccommodationTypePort;
import com.aogdev.rural.application.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AccommodationConfiguration {

    @Bean
    public AccommodationService accommodationService(
            SaveAccommodationPort saveAccommodationPort,
            LoadAccommodationPort loadAccommodationPort,
            FindAccommodationByNamePort findAccommodationByNamePort,
            ListAccommodationsPort listAccommodationsPort,
            LoadAccommodationTypePort loadAccommodationTypePort) {

        return new AccommodationService(
                saveAccommodationPort,
                loadAccommodationPort,
                findAccommodationByNamePort,
                listAccommodationsPort,
                loadAccommodationTypePort
        );
    }
}