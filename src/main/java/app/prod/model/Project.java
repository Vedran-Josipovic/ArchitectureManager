package app.prod.model;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import app.prod.enumeration.Status;

//To be implemented in the future
public final class Project extends Issue implements Issuable {
    private Client client;
    private Set<Task> tasks;
    private List<Transaction> transactions;
    private Set<Employee> employees;

    public Project() {
        super();
    }

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

    public Project(Long id, String name, String description, LocalDate startDate, LocalDate deadline, Status status, Client client, Set<Task> tasks, List<Transaction> transactions, Set<Employee> employees) {
        super(id, name, description, startDate, deadline, status);
        this.client = client;
        this.tasks = tasks;
        this.transactions = transactions;
        this.employees = employees;
    }

    public Project(String name, String description, LocalDate startDate, LocalDate deadline, Status status, Client client, Set<Task> tasks, List<Transaction> transactions, Set<Employee> employees) {
        super(name, description, startDate, deadline, status);
        this.client = client;
        this.tasks = tasks;
        this.transactions = transactions;
        this.employees = employees;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Project project = (Project) o;
        return Objects.equals(getClient(), project.getClient()) && Objects.equals(getTasks(), project.getTasks()) && Objects.equals(getTransactions(), project.getTransactions()) && Objects.equals(getEmployees(), project.getEmployees());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getClient(), getTasks(), getTransactions(), getEmployees());
    }

    @Override
    public String toString() {
        return "Project{" +
                "client=" + client +
                ", tasks=" + tasks +
                ", transactions=" + transactions +
                ", employees=" + employees +
                "} " + super.toString();
    }

}
