package slot;

import shared.ApplicationError;
import shared.ErrorCode;

public class SlotNoTrobatError extends ApplicationError {
    public SlotNoTrobatError(String message) {
        this.message = message;
        this.errorCode = ErrorCode.INVALID_REQUEST;
    }
}
