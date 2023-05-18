package slot.domain;

import shared.ApplicationError;
import shared.ErrorCode;

public class EstocInsuficientError extends ApplicationError {
    public EstocInsuficientError(String message) {
        this.message = message;
        this.errorCode = ErrorCode.INVALID_REQUEST;
    }
}
