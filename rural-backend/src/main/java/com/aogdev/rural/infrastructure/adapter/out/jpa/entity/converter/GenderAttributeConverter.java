package com.aogdev.rural.infrastructure.adapter.out.entity.converter;

import com.aogdev.rural.domain.enumerated.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderAttributeConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }
        return String.valueOf(gender.getCode());
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return Gender.fromCode(dbData.charAt(0));
    }
}
