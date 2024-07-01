package app.prod.model;

import app.prod.enumeration.Status;

public sealed interface Issuable permits Issue, Project, Task {
    boolean isCompleted();
    double getExpectedProgress();
}
