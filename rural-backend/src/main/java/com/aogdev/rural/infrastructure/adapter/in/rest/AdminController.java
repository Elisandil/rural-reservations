package com.aogdev.rural.infrastructure.adapter.in.rest;

import com.aogdev.rural.application.port.in.admin.*;
import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.AdminResponse;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.ChangePasswordRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.CreateAdminRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.dto.admin.UpdateAdminRequest;
import com.aogdev.rural.infrastructure.adapter.in.rest.mapper.AdminRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {
    private final CreateAdminUseCase createAdminUseCase;
    private final GetAdminUseCase getAdminUseCase;
    private final FindAdminUseCase findAdminUseCase;
    private final UpdateAdminUseCase updateAdminUseCase;
    private final ActivateAdminUseCase activateAdminUseCase;
    private final DeactivateAdminUseCase deactivateAdminUseCase;
    private final ChangeAdminPasswordUseCase changeAdminPasswordUseCase;
    private final ListAdminsUseCase listAdminsUseCase;

    @PostMapping
    public ResponseEntity<AdminResponse> createAdmin(@Valid @RequestBody
                                                         CreateAdminRequest request) {

        log.info("REST request to create admin with email: {}", request.email());

        CreateAdminCommand command = AdminRestMapper.toCommand(request);
        Admin admin = createAdminUseCase.create(command);
        AdminResponse response = AdminRestMapper.toResponse(admin);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> getAdmin(@PathVariable Long id) {
        log.info("REST request to get admin by id: {}", id);

        Admin admin = getAdminUseCase.getById(id);
        AdminResponse response = AdminRestMapper.toResponse(admin);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<AdminResponse> getAdminByEmail(@PathVariable String email) {
        log.info("REST request to get admin by email: {}", email);

        Optional<Admin> adminOpt = findAdminUseCase.findByEmail(new Email(email));

        return adminOpt
                .map(AdminRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> listAdmins(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        log.info("REST request to list admins (activeOnly: {})", activeOnly);

        List<Admin> admins = activeOnly
                ? listAdminsUseCase.listActive()
                : listAdminsUseCase.listAll();

        List<AdminResponse> response = AdminRestMapper.toResponseList(admins);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdminRequest request) {
        log.info("REST request to update admin with id: {}", id);

        UpdateAdminCommand command = AdminRestMapper.toCommand(id, request);
        Admin admin = updateAdminUseCase.update(command);
        AdminResponse response = AdminRestMapper.toResponse(admin);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateAdmin(@PathVariable Long id) {
        log.info("REST request to activate admin with id: {}", id);

        activateAdminUseCase.activate(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateAdmin(@PathVariable Long id) {
        log.info("REST request to deactivate admin with id: {}", id);

        deactivateAdminUseCase.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("REST request to change password for admin with id: {}", id);

        changeAdminPasswordUseCase.changePassword(id, request.newPassword());

        return ResponseEntity.noContent().build();
    }
}
