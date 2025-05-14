package info.mackiewicz.bankapp.core.user.exception;

public class InvalidMoneyValueException extends RuntimeException {
    public InvalidMoneyValueException(String message) {
        super(message);
    }
}
