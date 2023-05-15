package producte;

import shared.ApplicationError;
import shared.ErrorCode;

public class ProducteExistentError extends ApplicationError {
    public ProducteExistentError(String message) {
        this.message = message;
        this.errorCode = ErrorCode.INVALID_REQUEST;
    }
}
