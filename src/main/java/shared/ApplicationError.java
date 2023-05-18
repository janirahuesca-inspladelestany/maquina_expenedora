package shared;

public abstract class ApplicationError extends Exception {
    public ErrorCode errorCode;
    public String message;

    private Object metadata;

    public void withMetadata(Object metadata) {
        this.metadata = metadata;
    }

    public Object getMetadata() {
        return this.metadata;
    }
}
