package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class TransactionBuildingException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    public TransactionBuildingException(String message) {
        super(message, ERROR_CODE);
    }
    public TransactionBuildingException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
