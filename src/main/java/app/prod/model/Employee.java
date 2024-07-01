package app.prod.model;

import java.util.Objects;

public class Employee extends Contact {
    private String position;
    private Project project;

    public Employee(Long id, String name, String email, String position, Project project) {
        super(id, name, email);
        this.position = position;
        this.project = project;
    }

    public Employee() {
    }

    public Employee(String name, String email, String position, Project project) {
        super(name, email);
        this.position = position;
        this.project = project;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
        Employee employee = (Employee) o;
        return Objects.equals(getPosition(), employee.getPosition()) && Objects.equals(getProject(), employee.getProject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPosition(), getProject());
    }

    @Override
    public String toString() {
        return getName() + " [" + position + "]";
    }

    public String getEmployeeDetails() {
        return "Employee{" +
                "position='" + position + '\'' +
                ", project=" + project +
                "} " + super.toString();
    }
}
