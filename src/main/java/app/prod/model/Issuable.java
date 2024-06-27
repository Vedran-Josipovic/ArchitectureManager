package app.prod.model;

import app.prod.enumeration.Status;

public sealed interface Issuable permits Issue, Project, Task {
    default boolean isCompleted(Status status){
        return status.isDone();
    }
    double getExpectedProgress();
}
