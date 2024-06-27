package app.prod.model;

import java.util.List;
import java.util.Objects;

public class Client extends Entity {
    private String email;
    private String companyName;
    private List<Project> projects;

    public Client(Long id, String name, String email, String companyName, List<Project> projects) {
        super(id, name);
        this.email = email;
        this.companyName = companyName;
        this.projects = projects;
    }
    public Client(String name, String email, String companyName, List<Project> projects) {
        super(name);
        this.email = email;
        this.companyName = companyName;
        this.projects = projects;
    }
    public Client(Long id, String name, String email, String companyName) {
        super(id, name);
        this.email = email;
        this.companyName = companyName;
        this.projects = projects;
    }
    public Client(String name, String email, String companyName) {
        super(name);
        this.email = email;
        this.companyName = companyName;
        this.projects = projects;
    }

    public Client() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return Objects.equals(getEmail(), client.getEmail()) && Objects.equals(getCompanyName(), client.getCompanyName()) && Objects.equals(getProjects(), client.getProjects());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEmail(), getCompanyName(), getProjects());
    }

    @Override
    public String toString() {
        return getCompanyName();
    }
}
