package com.aogdev.rural.infrastructure.config;

import com.aogdev.rural.application.port.out.customer.*;
import com.aogdev.rural.application.port.out.reservation.LoadReservationPort;
import com.aogdev.rural.application.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CustomerConfiguration {

    @Bean
    public CustomerService customerService(
            SaveCustomerPort saveCustomerPort,
            LoadCustomerPort loadCustomerPort,
            FindCustomerByDniPort findCustomerByDniPort,
            DeleteCustomerPort deleteCustomerPort,
            ListCustomersPort listCustomersPort,
            LoadReservationPort loadReservationPort) {

        return new CustomerService(
                saveCustomerPort,
                loadCustomerPort,
                findCustomerByDniPort,
                deleteCustomerPort,
                listCustomersPort,
                loadReservationPort
        );
    }
}