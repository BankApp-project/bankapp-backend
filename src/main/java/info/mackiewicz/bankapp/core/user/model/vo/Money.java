package info.mackiewicz.bankapp.core.user.model.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value Object representing an amount of money.
 * Ensures proper validation for positive values.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class Money {
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    public static final Money TEN = new Money(BigDecimal.TEN);

    private BigDecimal value;

    /**
     * Constructor to initialize Money object with a value.
     *
     * @param value - The monetary value.
     */
    public Money(BigDecimal value) {
        validate(value);
        this.value = value.setScale(2, BigDecimal.ROUND_HALF_UP); // Round to 2 decimal places (standard for money)
    }

    /**
     * Validates the monetary value to ensure it's not null or negative.
     *
     * @param value - The monetary value to be validated.
     */
    private void validate(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Money value cannot be null");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money value cannot be negative");
        }
    }

    @Override
    public String toString() {
        return "$" + value.toString(); // Return value with a currency symbol
    }

    /**
     * Subtracts an amount from the current value and returns a new Money object.
     *
     * @param amount - Amount to subtract (must be greater than zero).
     * @return A new Money instance after subtraction.
     */
    public Money subtract(BigDecimal amount) {
        validateAmount(amount);
        return new Money(value.subtract(amount));
    }

    /**
     * Adds an amount to the current value and returns a new Money object.
     *
     * @param amount - Amount to add (must be greater than zero).
     * @return A new Money instance after addition.
     */
    public Money add(BigDecimal amount) {
        validateAmount(amount);
        return new Money(value.add(amount));
    }

    /**
     * Validates the amount to ensure it's greater than zero.
     *
     * @param amount - The amount to be validated.
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    /**
     * Doubles the current money value and returns a new Money object.
     * If the value is zero, it returns Money.ZERO, and if the value is 1, it returns Money.TEN.
     *
     * @return A new Money instance with doubled value.
     */
    public Money doubleValue() {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO;
        } else if (value.compareTo(BigDecimal.ONE) == 0) {
            return TEN;
        } else {
            return new Money(value.multiply(BigDecimal.valueOf(2)));
        }
    }
}
