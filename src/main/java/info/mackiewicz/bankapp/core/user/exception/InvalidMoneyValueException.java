package info.mackiewicz.bankapp.core.user.exception;

import info.mackiewicz.bankapp.core.user.model.interfaces.ErrorCode;

public class InvalidMoneyValueException extends AccountBaseException {

    public InvalidMoneyValueException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }
}