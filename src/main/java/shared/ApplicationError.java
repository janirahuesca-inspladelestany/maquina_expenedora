package shared;

public abstract class ApplicationError extends Exception {
    public ErrorCode errorCode;
    public String message;

    private Object metadata;

    public ApplicationError withMetadata(Object metadata) {
        this.metadata = metadata;
        return this;
    }

    public Object getMetadata() {
        return this.metadata;
    }
}
