package app.prod.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Client extends Contact implements Serializable {
    private String companyName;
    private List<Project> projects;

    public Client(Long id, String name, String email, String companyName, List<Project> projects) {
        super(id, name, email);
        this.companyName = companyName;
        this.projects = projects;
    }

    public Client(Long id, String name, String email, String companyName) {
        super(id, name, email);
        this.companyName = companyName;
    }

    public Client(String name, String email, String companyName) {
        super(name, email);
        this.companyName = companyName;
    }



    public Client() {

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
    public String toString() {
        return getCompanyName();
    }
}
