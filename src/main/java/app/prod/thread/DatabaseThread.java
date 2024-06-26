package app.prod.thread;

public abstract class DatabaseThread {
    public static Boolean isDatabaseOperationInProgress = false;

    protected synchronized void startDatabaseOperation() throws InterruptedException {
        while (isDatabaseOperationInProgress) {
            wait();
        }
        isDatabaseOperationInProgress = true;
    }

    protected synchronized void endDatabaseOperation() {
        isDatabaseOperationInProgress = false;
        notifyAll();
    }
}
