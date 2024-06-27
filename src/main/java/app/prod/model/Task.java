package app.prod.model;

import app.prod.enumeration.Status;

import java.time.LocalDate;
import java.util.Objects;

//Add validation to the Task add controller to ensure that the start date is before the deadline.
public final class Task extends Issue implements Issuable{
    private Employee assignee;
    private Project project;

    public Task(Long id, String name, String description, LocalDate startDate, LocalDate deadline, Status status, Employee assignee, Project project) {
        super(id, name, description, startDate, deadline, status);
        this.assignee = assignee;
        this.project = project;
    }

    public Task(String name, String description, LocalDate startDate, LocalDate deadline, Status status, Employee assignee, Project project) {
        super(name, description, startDate, deadline, status);
        this.assignee = assignee;
        this.project = project;
    }

    @Override
    public double getExpectedProgress() {
        LocalDate startDate = super.getStartDate();
        LocalDate endDate = super.getDeadline();
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
        return Objects.equals(getAssignee(), task.getAssignee()) && Objects.equals(getProject(), task.getProject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAssignee(), getProject());
    }

    @Override
    public String toString() {
        return "Task{" +
                "assignee=" + assignee +
                ", project=" + project +
                "} " + super.toString();
    }
}
