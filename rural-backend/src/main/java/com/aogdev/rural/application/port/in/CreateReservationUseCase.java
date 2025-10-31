package com.aogdev.rural.application.port.in;

import com.aogdev.rural.application.command.CreateReservationCommand;
import com.aogdev.rural.domain.model.Reservation;

public interface CreateReservationUseCase {
    Reservation execute(CreateReservationCommand command);
}
