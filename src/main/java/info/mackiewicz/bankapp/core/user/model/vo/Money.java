package info.mackiewicz.bankapp.core.user.model.vo;

import info.mackiewicz.bankapp.core.user.exception.InvalidMoneyValueException; // Importujemy nasz wyjątek

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
        if (!isValid(value)) {
            throw new InvalidMoneyValueException("Money value must be greater than zero.");
        }
        this.value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Checks if the monetary value is valid (greater than zero).
     *
     * @param value - The monetary value to be checked.
     * @return true if value is greater than zero, false otherwise.
     */
    public boolean isValid(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return "$" + value.toString();
    }

    /**
     * Subtracts an amount from the current value and returns a new Money object.
     *
     * @param amount - Amount to subtract (must be greater than zero).
     * @return A new Money instance after subtraction.
     */
    public Money subtract(BigDecimal amount) {
        if (!isValid(amount)) {
            throw new InvalidMoneyValueException("Amount must be greater than zero");
        }
        return new Money(value.subtract(amount));
    }

    /**
     * Adds an amount to the current value and returns a new Money object.
     *
     * @param amount - Amount to add (must be greater than zero).
     * @return A new Money instance after addition.
     */
    public Money add(BigDecimal amount) {
        if (!isValid(amount)) {
            throw new InvalidMoneyValueException("Amount must be greater than zero");
        }
        return new Money(value.add(amount));
    }
}
