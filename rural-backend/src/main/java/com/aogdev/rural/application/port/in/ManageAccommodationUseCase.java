package com.aogdev.rural.application.port.in;

import com.aogdev.rural.application.command.CreateAccommodationCommand;
import com.aogdev.rural.application.command.UpdateAccommodationCommand;
import com.aogdev.rural.domain.model.Accommodation;

public interface ManageAccommodationUseCase {
    Accommodation create(CreateAccommodationCommand command);
    Accommodation update(UpdateAccommodationCommand command);
    void activate(Long accommodationId);
    void deactivate(Long accommodationId);
}
