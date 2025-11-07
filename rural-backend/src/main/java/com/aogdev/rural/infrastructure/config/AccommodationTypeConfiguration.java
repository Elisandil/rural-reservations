package com.aogdev.rural.infrastructure.config;

import com.aogdev.rural.application.port.out.accommodationType.*;
import com.aogdev.rural.application.service.AccommodationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AccommodationTypeConfiguration {

    @Bean
    public AccommodationTypeService accommodationTypeService(
            SaveAccommodationTypePort saveAccommodationTypePort,
            LoadAccommodationTypePort loadAccommodationTypePort,
            FindAccommodationTypeByNamePort findAccommodationTypeByNamePort,
            ListAccommodationTypesPort listAccommodationTypesPort,
            DeleteAccommodationTypePort deleteAccommodationTypePort) {

        return new AccommodationTypeService(
                saveAccommodationTypePort,
                loadAccommodationTypePort,
                findAccommodationTypeByNamePort,
                listAccommodationTypesPort,
                deleteAccommodationTypePort
        );
    }
}