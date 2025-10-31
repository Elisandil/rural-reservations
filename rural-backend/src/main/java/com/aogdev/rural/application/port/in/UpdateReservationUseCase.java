package com.aogdev.rural.application.port.in;

import com.aogdev.rural.application.command.UpdateReservationCommand;
import com.aogdev.rural.domain.model.Reservation;

public interface UpdateReservationUseCase {
    Reservation update(UpdateReservationCommand command);
    Reservation markAsPaid(Long reservationId);
}
