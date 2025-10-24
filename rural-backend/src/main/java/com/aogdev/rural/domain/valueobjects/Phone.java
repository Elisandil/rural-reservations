package com.aogdev.rural.domain.valueobjects;

public record Phone(String value) {

    public Phone {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("The phone cannot be null or blank");
        }
        String cleanedValue = value.replaceAll("[^0-9+]", "");

        if (cleanedValue.length() < 9 || cleanedValue.length() > 15) {
            throw new IllegalArgumentException("Invalid phone number: " + value);
        }
        value = cleanedValue;
    }

    public String formatted() {

        if (value.startsWith("+34") && value.length() == 12) {
            return String.format("+34 %s %s %s",
                    value.substring(3, 6),
                    value.substring(6,9),
                    value.substring(9)
            );
        }
        return value;
    }
}
