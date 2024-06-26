package app.prod.model;

import app.prod.enumeration.Status;

import java.time.LocalDate;

public sealed interface Issue permits Project, Task {
    default boolean isCompleted(Status status){
        return status.isDone();
    }
    double getExpectedProgress();
}
