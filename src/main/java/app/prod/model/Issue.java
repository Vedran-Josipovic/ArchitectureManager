package app.prod.model;

import app.prod.enumeration.Status;

import java.time.LocalDate;
import java.util.Objects;

public sealed abstract class Issue extends Entity implements Issuable permits Project{
    private String description;
    private LocalDate startDate, deadline;
    private Status status;

    public Issue(Long id, String name, String description, LocalDate startDate, LocalDate deadline, Status status) {
        super(id, name);
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
    }

    public Issue(String name, String description, LocalDate startDate, LocalDate deadline, Status status) {
        super(name);
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
    }

    public Issue() {

    }

    public Issue(String name){
        super(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Issue issue = (Issue) o;
        return Objects.equals(getDescription(), issue.getDescription()) && Objects.equals(getStartDate(), issue.getStartDate()) && Objects.equals(getDeadline(), issue.getDeadline()) && getStatus() == issue.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDescription(), getStartDate(), getDeadline(), getStatus());
    }

    @Override
    public String toString() {
        return "Issue{" +
                "description='" + description + '\'' +
                ", startDate=" + startDate +
                ", deadline=" + deadline +
                ", status=" + status +
                "} " + super.toString();
    }
}
