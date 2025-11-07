package com.aogdev.rural.domain.valueobject;

import com.aogdev.rural.domain.exception.InvalidDomainObjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create valid money with BigDecimal")
    void shouldCreateValidMoneyWithBigDecimal() {
        BigDecimal amount = new BigDecimal("99.99");
        Currency currency = Currency.getInstance("EUR");

        Money money = new Money(amount, currency);

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(money.currency()).isEqualTo(currency);
    }

    @Test
    @DisplayName("Should create euros with BigDecimal")
    void shouldCreateEurosWithBigDecimal() {
        Money money = Money.euros(new BigDecimal("50.75"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("50.75"));
        assertThat(money.currency().getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should create euros with double")
    void shouldCreateEurosWithDouble() {
        Money money = Money.euros(50.75);

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("50.75"));
        assertThat(money.currency().getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should round to two decimal places")
    void shouldRoundToTwoDecimalPlaces() {
        Money money = new Money(new BigDecimal("99.999"), Currency.getInstance("EUR"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(money.value().scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle values with fewer than two decimals")
    void shouldHandleValuesWithFewerThanTwoDecimals() {
        Money money = Money.euros(new BigDecimal("50"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(money.value().scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> new Money(null, Currency
                .getInstance("EUR")))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("value cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when value is negative")
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThatThrownBy(() -> new Money(new BigDecimal("-10.00"), Currency
                .getInstance("EUR")))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("value cannot be negative");
    }

    @Test
    @DisplayName("Should throw exception when currency is null")
    void shouldThrowExceptionWhenCurrencyIsNull() {
        assertThatThrownBy(() -> new Money(new BigDecimal("50.00"),
                null))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("currency cannot be null");
    }

    @Test
    @DisplayName("Should accept zero value")
    void shouldAcceptZeroValue() {
        Money money = Money.euros(BigDecimal.ZERO);

        assertThat(money.value()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should add money with same currency")
    void shouldAddMoneyWithSameCurrency() {
        Money money1 = Money.euros(50.00);
        Money money2 = Money.euros(25.50);

        Money result = money1.add(money2);

        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("75.50"));
        assertThat(result.currency().getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should throw exception when adding different currencies")
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money euros = Money.euros(50.00);
        Money dollars = new Money(new BigDecimal("50.00"), Currency.getInstance("USD"));

        assertThatThrownBy(() -> euros.add(dollars))
                .isInstanceOf(InvalidDomainObjectException.class)
                .hasMessageContaining("cannot add different currencies");
    }

    @Test
    @DisplayName("Should multiply money by positive factor")
    void shouldMultiplyMoneyByPositiveFactor() {
        Money money = Money.euros(25.00);

        Money result = money.multiply(3);

        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(result.currency().getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should multiply money by one")
    void shouldMultiplyMoneyByOne() {
        Money money = Money.euros(50.00);

        Money result = money.multiply(1);

        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Should multiply money by zero")
    void shouldMultiplyMoneyByZero() {
        Money money = Money.euros(50.00);

        Money result = money.multiply(0);

        assertThat(result.value()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"EUR", "USD", "GBP", "JPY"})
    @DisplayName("Should support different currencies")
    void shouldSupportDifferentCurrencies(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        Money money = new Money(new BigDecimal("100.00"), currency);

        assertThat(money.currency()).isEqualTo(currency);
        assertThat(money.currency().getCurrencyCode()).isEqualTo(currencyCode);
    }

    @Test
    @DisplayName("Should preserve immutability when adding")
    void shouldPreserveImmutabilityWhenAdding() {
        Money original = Money.euros(50.00);
        Money toAdd = Money.euros(25.00);

        Money result = original.add(toAdd);

        assertThat(result).isNotSameAs(original);
        assertThat(original.value()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("75.00"));
    }

    @Test
    @DisplayName("Should preserve immutability when multiplying")
    void shouldPreserveImmutabilityWhenMultiplying() {
        Money original = Money.euros(50.00);

        Money result = original.multiply(2);

        assertThat(result).isNotSameAs(original);
        assertThat(original.value()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should handle large amounts")
    void shouldHandleLargeAmounts() {
        Money money = Money.euros(new BigDecimal("999999.99"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("999999.99"));
    }

    @Test
    @DisplayName("Should handle small fractional amounts")
    void shouldHandleSmallFractionalAmounts() {
        Money money = Money.euros(new BigDecimal("0.01"));

        assertThat(money.value()).isEqualByComparingTo(new BigDecimal("0.01"));
    }
}