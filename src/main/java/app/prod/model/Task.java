package app.prod.model;

import app.prod.enumeration.Status;

import java.time.LocalDate;
import java.util.Objects;

//Add validation to the Task add controller to ensure that the start date is before the deadline.
public final class Task extends Entity implements Issue{
    private String description;
    private LocalDate startDate, deadline;
    private Status status;
    private Employee assignee;
    private Project project;

    @Override
    public double getExpectedProgress() {
        LocalDate startDate = getStartDate();
        LocalDate endDate = getDeadline();
        LocalDate currentDate = LocalDate.now();

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return 0.0;
        }

        long totalDuration = startDate.until(endDate).getDays();
        long elapsedDuration = startDate.until(currentDate).getDays();

        if (elapsedDuration >= totalDuration) {
            return 100.0;
        }

        return (double) elapsedDuration / totalDuration * 100;
    }

    public Task(Long id, String name, String description, LocalDate startDate, LocalDate deadline, Status status, Employee assignee, Project project) {
        super(id, name);
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
        this.assignee = assignee;
        this.project = project;
    }
    public Task(String name, String description, LocalDate startDate, LocalDate deadline, Status status, Employee assignee, Project project) {
        super(name);
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
        this.assignee = assignee;
        this.project = project;
    }
    public Task(Long id, String name, String description, LocalDate startDate, LocalDate deadline, Status status) {
        super(id, name);
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
    }
    public Task(String name, String description, LocalDate startDate, LocalDate deadline, Status status) {
        super(name);
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
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

    public Employee getAssignee() {
        return assignee;
    }

    public void setAssignee(Employee assignee) {
        this.assignee = assignee;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Task task = (Task) o;
        return Objects.equals(getDescription(), task.getDescription()) && Objects.equals(getStartDate(), task.getStartDate()) && Objects.equals(getDeadline(), task.getDeadline()) && getStatus() == task.getStatus() && Objects.equals(getAssignee(), task.getAssignee()) && Objects.equals(getProject(), task.getProject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDescription(), getStartDate(), getDeadline(), getStatus(), getAssignee(), getProject());
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", startDate=" + startDate +
                ", deadline=" + deadline +
                ", status=" + status +
                ", assignee=" + assignee +
                ", project=" + project +
                "} " + super.toString();
    }
}
