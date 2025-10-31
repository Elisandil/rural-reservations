package com.aogdev.rural.domain.exception;

public class EntityNotFoundException extends DomainException {
    private final String entityType;
    private final Object identifier;

    public EntityNotFoundException(String entityType, Object identifier) {
        super(String.format("%s not found with identifier: %s", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public String getEntityType() {
        return entityType;
    }

    public Object getIdentifier() {
        return identifier;
    }
}