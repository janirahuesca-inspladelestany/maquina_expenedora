package daos;

import org.example.ApplicationError;
import org.example.ErrorCode;

public class InfrastructureError extends ApplicationError {
    public InfrastructureError(String message) {
        this.message = message;
        this.errorCode = ErrorCode.INFRASTRUCTURE_ERROR;
    }
}
