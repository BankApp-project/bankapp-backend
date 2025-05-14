package info.mackiewicz.bankapp.core.user.model.vo;

import info.mackiewicz.bankapp.core.user.exception.InvalidMoneyValueException;
import info.mackiewicz.bankapp.core.user.model.interfaces.ErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    public static final Money TEN = new Money(BigDecimal.TEN);
    private BigDecimal value;

    public Money(BigDecimal value) {
        if (!isValid(value)) {
            throw new InvalidMoneyValueException(ErrorCode.INVALID_MONEY_VALUE);
        }
        this.value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public boolean isValid(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return "$" + value.toString();
    }

    public Money subtract(BigDecimal amount) {
        if (!isValid(amount)) {
            throw new InvalidMoneyValueException(ErrorCode.INVALID_MONEY_VALUE);
        }
        return new Money(value.subtract(amount));
    }

    public Money add(BigDecimal amount) {
        if (!isValid(amount)) {
            throw new InvalidMoneyValueException(ErrorCode.INVALID_MONEY_VALUE);
        }
        return new Money(value.add(amount));
    }
}