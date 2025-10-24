package com.aogdev.rural.domain.valueobjects;

public record PersonName(String firstName, String surnames) {

    public PersonName {

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null, or blank");
        }
        if (surnames == null || surnames.isBlank()) {
            throw new IllegalArgumentException("Surnames cannot be null, nor blank");
        }
    }

    public String fullName() {
        String[] firstNameArray = firstName.trim().split("\\s+");
        String[] surnameArray = surnames.trim().split("\\s+");

        StringBuilder sb = new StringBuilder();

        for (String name : firstNameArray) {
            sb.append(name).append(" ");
        }

        if (surnameArray.length == 1) {
            return sb.append(surnameArray[0]).toString();
        }
        else {
            return sb.append(surnameArray[0])
                    .append(" ")
                    .append(surnameArray[1])
                    .toString();
        }
    }
}
