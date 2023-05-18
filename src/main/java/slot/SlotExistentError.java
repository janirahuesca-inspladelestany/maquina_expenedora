package slot;

import shared.ApplicationError;
import shared.ErrorCode;

public class SlotExistentError extends ApplicationError {
    public SlotExistentError(String message) {
        this.message = message;
        this.errorCode = ErrorCode.INVALID_REQUEST;
    }
}
