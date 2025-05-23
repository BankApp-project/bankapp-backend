package info.mackiewicz.bankapp.system.banking.history.exception;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

/**
 * Base class for all exceptions thrown by the TransactionHistoryService.
 */
public abstract class TransactionHistoryBaseException extends BankAppBaseException {

    public TransactionHistoryBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TransactionHistoryBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
