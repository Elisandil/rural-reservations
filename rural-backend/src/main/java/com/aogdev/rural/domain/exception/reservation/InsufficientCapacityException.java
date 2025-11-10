package com.aogdev.rural.domain.exception.reservation;

import com.aogdev.rural.domain.exception.DomainException;

public class InsufficientCapacityException extends DomainException {

    public InsufficientCapacityException(Integer requested, Integer available) {
        super(String.format("Insufficient capacity: requested %d beds but only %d available", requested, available));
    }
}