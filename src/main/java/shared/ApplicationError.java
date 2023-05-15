package shared;

public abstract class ApplicationError extends Exception {
    public ErrorCode errorCode;
    public String message;
}
