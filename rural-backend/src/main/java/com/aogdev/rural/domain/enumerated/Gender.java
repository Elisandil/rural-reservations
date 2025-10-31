package com.aogdev.rural.domain.enumerated;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;

public enum Gender {
    MALE('M'),
    FEMALE('F'),
    OTHER('O');

    private final char code;

    Gender(char code) {
        this.code = code;
    }

    public static Gender fromCode(char code) {
        for (Gender gender : values()) {
            if (gender.code == code) {
                return gender;
            }
        }
        throw new InvalidDomainObjectException("Gender", "invalid code: " + code);
    }

    public char getCode() {
        return code;
    }
}


