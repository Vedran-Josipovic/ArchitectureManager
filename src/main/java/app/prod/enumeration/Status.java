package app.prod.enumeration;

public enum Status {
    TO_DO,
    IN_PROGRESS,
    DONE;

    public boolean isDone() {
        return this.equals(DONE);
    }
    public boolean isInProgress() {
        return this.equals(IN_PROGRESS);
    }
}
