package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class TransactionTypeNotSpecifiedException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Transaction type is required.";

    public TransactionTypeNotSpecifiedException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_TYPE_MISSING);
    }

    public TransactionTypeNotSpecifiedException(String message) {
        super(message, ErrorCode.TRANSACTION_TYPE_MISSING);
    }
}
