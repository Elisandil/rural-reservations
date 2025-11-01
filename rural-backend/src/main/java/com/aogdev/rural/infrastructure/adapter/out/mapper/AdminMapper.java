package com.aogdev.rural.infrastructure.adapter.out.mapper;

import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobjects.Email;
import com.aogdev.rural.domain.valueobjects.PersonName;
import com.aogdev.rural.domain.valueobjects.Phone;
import com.aogdev.rural.infrastructure.adapter.out.entity.AdminEntity;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public Admin toDomain(AdminEntity entity) {

        if (entity == null) {
            return null;
        }
        String[] nameParts = entity.getName().split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String surnames = nameParts.length > 1 ? nameParts[1] : "";

        return new Admin(
            entity.getId(),
            new PersonName(firstName, surnames),
            new Email(entity.getEmail()),
            new Phone(entity.getPhone()),
            entity.getPassword(),
            entity.getActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public AdminEntity toEntity(Admin admin) {

        if (admin == null) {
            return null;
        }

        return AdminEntity.builder()
                .id(admin.id())
                .name(admin.name().fullName())
                .email(admin.email().value())
                .phone(admin.phone().value())
                .password(admin.passwordHash())
                .active(admin.active())
                .build();
    }

    public void updateEntity(Admin admin, AdminEntity entity) {

        if (admin == null || entity == null) {
            return ;
        }

        entity = AdminEntity.builder()
                .id(entity.getId())
                .name(admin.name().fullName())
                .email(admin.email().value())
                .phone(admin.phone().value())
                .password(admin.passwordHash())
                .active(admin.active())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
