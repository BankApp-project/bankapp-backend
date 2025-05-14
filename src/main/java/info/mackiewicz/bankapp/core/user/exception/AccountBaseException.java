package info.mackiewicz.bankapp.core.user.exception;

public class AccountBaseException extends RuntimeException {
    private final String code;

    public AccountBaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}