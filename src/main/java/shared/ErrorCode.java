package shared;

public enum ErrorCode {
    INFRASTRUCTURE_ERROR("001"),
    INVALID_REQUEST("002"),
    UNEXPECTED_ERROR("003");

    String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
