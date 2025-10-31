package com.aogdev.rural.domain.valueobjects;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;

import java.math.BigDecimal;
import java.util.Currency;
import java.math.RoundingMode;

public record Money(BigDecimal value, Currency currency) {

    public Money {
        if (value == null) {
            throw new InvalidDomainObjectException("Money", "value cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidDomainObjectException("Money", "value cannot be negative");
        }
        if (currency == null) {
            throw new InvalidDomainObjectException("Money", "currency cannot be null");
        }

        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money euros(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("EUR"));
    }

    public static Money euros(double amount) {
        return euros(BigDecimal.valueOf(amount));
    }

    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new InvalidDomainObjectException("Money", "cannot add different currencies");
        }
        return new Money(value.add(other.value), currency);
    }

    public Money multiply(int factor) {
        return new Money(value.multiply(BigDecimal.valueOf(factor)), currency);
    }
}