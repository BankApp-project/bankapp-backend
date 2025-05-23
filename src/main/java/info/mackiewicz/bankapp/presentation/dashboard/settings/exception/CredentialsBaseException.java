package info.mackiewicz.bankapp.presentation.dashboard.settings.exception;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public abstract class CredentialsBaseException extends BankAppBaseException {

    public CredentialsBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    
    public CredentialsBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

}
