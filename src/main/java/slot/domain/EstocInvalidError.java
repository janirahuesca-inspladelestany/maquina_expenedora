package slot.domain;

import shared.ApplicationError;
import shared.ErrorCode;

public class EstocInvalidError extends ApplicationError {

    public EstocInvalidError(String message) {
        this.message = message;
        this.errorCode = ErrorCode.INVALID_REQUEST;
    }
}
