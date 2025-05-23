package info.mackiewicz.bankapp.presentation.auth.recovery.password.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class TokenCreationException extends TokenException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;
    
    public TokenCreationException(String message) {
        super(message, ERROR_CODE);
    }
    public TokenCreationException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }
}
