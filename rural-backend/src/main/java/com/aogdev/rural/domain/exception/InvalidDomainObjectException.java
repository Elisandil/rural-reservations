package com.aogdev.rural.domain.exception;

public class InvalidDomainObjectException extends DomainException {

    public InvalidDomainObjectException(String objectType, String message) {
        super(String.format("Invalid %s: %s", objectType, message));
    }

    public InvalidDomainObjectException(String message) {
        super(message);
    }
}
