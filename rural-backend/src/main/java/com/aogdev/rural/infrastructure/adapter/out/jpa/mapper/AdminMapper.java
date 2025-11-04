package com.aogdev.rural.infrastructure.adapter.out.jpa.mapper;

import com.aogdev.rural.domain.model.Admin;
import com.aogdev.rural.domain.valueobject.Email;
import com.aogdev.rural.domain.valueobject.PersonName;
import com.aogdev.rural.domain.valueobject.Phone;
import com.aogdev.rural.infrastructure.adapter.out.jpa.entity.AdminJpaEntity;

public class AdminMapper {

    public static Admin toDomain(AdminJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Admin(
                entity.getId(),
                new PersonName(entity.getFirstName(), entity.getSurnames()),
                new Email(entity.getEmail()),
                new Phone(entity.getPhone()),
                entity.getPasswordHash(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static AdminJpaEntity toEntity(Admin admin) {
        if (admin == null) {
            return null;
        }

        return AdminJpaEntity.builder()
                .id(admin.id())
                .firstName(admin.name().firstName())
                .surnames(admin.name().surnames())
                .email(admin.email().value())
                .phone(admin.phone().value())
                .passwordHash(admin.passwordHash())
                .active(admin.active())
                .createdAt(admin.createdAt())
                .updatedAt(admin.updatedAt())
                .build();
    }
}
