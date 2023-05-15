package shared;

public class InfrastructureError extends ApplicationError {
    public InfrastructureError(String message) {
        this.message = message;
        this.errorCode = ErrorCode.INFRASTRUCTURE_ERROR;
    }
}
