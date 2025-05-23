package info.mackiewicz.bankapp.presentation.dashboard.settings.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class PasswordSameException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.PASSWORD_SAME;

    public PasswordSameException(String message) {
        super(message, ERROR_CODE);
    }

    public PasswordSameException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
