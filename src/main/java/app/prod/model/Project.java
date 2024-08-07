package app.prod.model;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import app.prod.enumeration.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class Project extends Issue implements Issuable, Serializable {
    private final static Logger logger = LoggerFactory.getLogger(Project.class);
    private Client client;
    private Set<Transaction> transactions;
    private Set<Employee> employees;

    public Project() {
        super();
    }

    public Project(String name){
        super(name);
    }



    @Override
    public boolean isCompleted() {
        return getStatus() == Status.DONE;
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
        logger.debug(this + ": Total Duration: " + totalDuration);
        logger.debug(this + ": Elapsed Duration: " + elapsedDuration);

        if (elapsedDuration >= totalDuration || currentDate.isAfter(endDate)) {
            return 100.0;
        }


        return (double) elapsedDuration / totalDuration * 100;
    }

    public Project(Long id, String name, String description, LocalDate startDate, LocalDate deadline, Status status, Client client, Set<Transaction> transactions, Set<Employee> employees) {
        super(id, name, description, startDate, deadline, status);
        this.client = client;
        this.transactions = transactions;
        this.employees = employees;
    }

    public Project(String name, String description, LocalDate startDate, LocalDate deadline, Status status, Client client, Set<Transaction> transactions, Set<Employee> employees) {
        super(name, description, startDate, deadline, status);
        this.client = client;
        this.transactions = transactions;
        this.employees = employees;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
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
        return Objects.equals(getClient(), project.getClient()) && Objects.equals(getTransactions(), project.getTransactions()) && Objects.equals(getEmployees(), project.getEmployees());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getClient(), getTransactions(), getEmployees());
    }

    @Override
    public String toString() {
        return getName();
    }

    public String displayProject(){
        return "Project{" +
                "client=" + client +
                ", transactions=" + transactions +
                ", employees=" + employees +
                "} " + super.toString();
    }

}
