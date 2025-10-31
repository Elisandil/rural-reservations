package com.aogdev.rural.domain.exception;

public class InsufficientCapacityException extends DomainException {

    public InsufficientCapacityException(int requested, int available) {
        super(String.format("Insufficient capacity: requested %d beds, but only %d available",
                requested, available));
    }
}
