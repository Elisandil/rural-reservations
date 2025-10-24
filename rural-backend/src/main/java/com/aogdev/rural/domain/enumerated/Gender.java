package com.aogdev.rural.domain.enumerated;

public enum Gender {
    MALE('M'),
    FEMALE('F'),
    OTHER('O');

    private final char code;

    Gender(char code) {
        this.code = code;
    }

    public static Gender fromCode(char code) {

        for(Gender gender : values()) {

            if (gender.code == code) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender code: " + code);
    }
}


