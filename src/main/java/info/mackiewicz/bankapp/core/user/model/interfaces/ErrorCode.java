package info.mackiewicz.bankapp.core.user.model.interfaces;

public enum ErrorCode {
    INVALID_MONEY_VALUE("1001", "Money value must be greater than zero");

    private String code;
    private String message;

    // Konstruktor
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}