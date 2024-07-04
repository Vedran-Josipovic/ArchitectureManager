package app.prod.service;

import app.prod.model.*;
import app.prod.utils.DatabaseUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseService {
    public static List<Location> getLocationsByType(String type) {
        List<Location> allLocations = DatabaseUtils.getLocations();
        return allLocations.stream()
                .filter(location -> {
                    if (type == null) return false;
                    if (type.equals("Address") && location instanceof Address) return true;
                    if (type.equals("VirtualLocation") && location instanceof VirtualLocation) return true;
                    return false;
                })
                .collect(Collectors.toList());
    }
    public static List<Transaction> getTransactionsByFilters(Transaction transactionFilter, BigDecimal minAmount, BigDecimal maxAmount) {
        List<Transaction> transactions = DatabaseUtils.getTransactions();
        return transactions.stream()
                .filter(t -> Optional.ofNullable(transactionFilter.getName()).filter(s -> !s.isEmpty())
                        .map(name -> t.getName().toLowerCase().contains(name.toLowerCase()))
                        .orElse(true))
                .filter(t -> Optional.ofNullable(transactionFilter.getTransactionType())
                        .map(type -> t.getTransactionType().equals(type))
                        .orElse(true))
                .filter(t -> Optional.ofNullable(transactionFilter.getDate())
                        .map(date -> t.getDate().equals(date))
                        .orElse(true))
                .filter(t -> Optional.ofNullable(minAmount)
                        .map(min -> t.getAmount().compareTo(min) >= 0)
                        .orElse(true))
                .filter(t -> Optional.ofNullable(maxAmount)
                        .map(max -> t.getAmount().compareTo(max) <= 0)
                        .orElse(true))
                .filter(t -> Optional.ofNullable(transactionFilter.getProject())
                        .map(project -> t.getProject().getId().equals(project.getId()))
                        .orElse(true))
                .collect(Collectors.toList());
    }
    public static List<Client> getClientsByFilters(Client clientFilter) {
        List<Client> clients = DatabaseUtils.getClients();
        return clients.stream()
                .filter(c -> Optional.ofNullable(clientFilter.getId())
                        .map(id -> c.getId().equals(id))
                        .orElse(true))
                .filter(c -> Optional.ofNullable(clientFilter.getName()).filter(s -> !s.isEmpty())
                        .map(name -> c.getName().toLowerCase().contains(name.toLowerCase()))
                        .orElse(true))
                .filter(c -> Optional.ofNullable(clientFilter.getEmail()).filter(s -> !s.isEmpty())
                        .map(email -> c.getEmail().toLowerCase().contains(email.toLowerCase()))
                        .orElse(true))
                .filter(c -> Optional.ofNullable(clientFilter.getCompanyName()).filter(s -> !s.isEmpty())
                        .map(companyName -> c.getCompanyName().toLowerCase().contains(companyName.toLowerCase()))
                        .orElse(true))
                .collect(Collectors.toList());
    }
    public static List<Project> getProjectsByFilters(Project projectFilter) {
        List<Project> projects = DatabaseUtils.getProjects();
        return projects.stream()
                .filter(p -> Optional.ofNullable(projectFilter.getName()).filter(s -> !s.isEmpty())
                        .map(name -> p.getName().toLowerCase().contains(name.toLowerCase()))
                        .orElse(true))
                .filter(p -> Optional.ofNullable(projectFilter.getDescription()).filter(s -> !s.isEmpty())
                        .map(description -> p.getDescription().toLowerCase().contains(description.toLowerCase()))
                        .orElse(true))
                .filter(p -> Optional.ofNullable(projectFilter.getStartDate())
                        .map(startDate -> !p.getStartDate().isBefore(startDate))
                        .orElse(true))
                .filter(p -> Optional.ofNullable(projectFilter.getDeadline())
                        .map(deadline -> !p.getDeadline().isAfter(deadline))
                        .orElse(true))
                .filter(p -> Optional.ofNullable(projectFilter.getStatus())
                        .map(status -> p.getStatus().equals(status))
                        .orElse(true))
                .filter(p -> Optional.ofNullable(projectFilter.getClient())
                        .map(client -> p.getClient().getId().equals(client.getId()))
                        .orElse(true))
                .collect(Collectors.toList());
    }
//    public static List<Project> getProjectsByClientId(Long clientId) {
//        return DatabaseUtils.getProjects().stream()
//                .filter(p -> p.getClient().getId().equals(clientId))
//                .collect(Collectors.toList());
//    }
//    public static Client getClientById(Long clientId) {
//        return DatabaseUtils.getClients().stream()
//                .filter(client -> client.getId().equals(clientId))
//                .findFirst()
//                .orElse(null);
//    }
//    public static Set<Transaction> getTransactionsByProjectId(Long projectId) {
//        return DatabaseUtils.getTransactions().stream()
//                .filter(transaction -> transaction.getProject().getId().equals(projectId))
//                .collect(Collectors.toSet());
//    }
    public static List<Employee> getEmployeesByFilters(Employee employeeFilter) {
        return DatabaseUtils.getEmployees().stream()
                .filter(e -> Optional.ofNullable(employeeFilter.getId())
                        .map(id -> e.getId().equals(id))
                        .orElse(true))
                .filter(e -> Optional.ofNullable(employeeFilter.getName()).filter(s -> !s.isEmpty())
                        .map(name -> e.getName().toLowerCase().contains(name.toLowerCase()))
                        .orElse(true))
                .filter(e -> Optional.ofNullable(employeeFilter.getEmail()).filter(s -> !s.isEmpty())
                        .map(email -> e.getEmail().toLowerCase().contains(email.toLowerCase()))
                        .orElse(true))
                .filter(e -> Optional.ofNullable(employeeFilter.getPosition()).filter(s -> !s.isEmpty())
                        .map(position -> e.getPosition().toLowerCase().contains(position.toLowerCase()))
                        .orElse(true))
                .filter(e -> Optional.ofNullable(employeeFilter.getProject())
                        .map(project -> e.getProject().getId().equals(project.getId()))
                        .orElse(true))
                .collect(Collectors.toList());
    }

    public static List<Client> sortClients(List<Client> clients, String sortBy) {
        return clients.stream()
                .sorted((c1, c2) -> {
                    if (sortBy == null) return 0;
                    if (sortBy.equals("Name")) return c1.getName().compareTo(c2.getName());
                    if (sortBy.equals("Email")) return c1.getEmail().compareTo(c2.getEmail());
                    if (sortBy.equals("Company Name")) return c1.getCompanyName().compareTo(c2.getCompanyName());
                    return 0;
                })
                .collect(Collectors.toList());
    }

    public static List<Employee> sortEmployees(List<Employee> employees, String sortBy) {
        return employees.stream()
                .sorted((e1, e2) -> {
                    if (sortBy == null) return 0;
                    if (sortBy.equals("Name")) return e1.getName().compareTo(e2.getName());
                    if (sortBy.equals("Email")) return e1.getEmail().compareTo(e2.getEmail());
                    if (sortBy.equals("Position")) return e1.getPosition().compareTo(e2.getPosition());
                    return 0;
                })
                .collect(Collectors.toList());
    }

    public static List<Location> sortLocations(List<Location> locations) {
        return locations.stream()
                .sorted(Comparator.comparing(Location::getFullLocationDetails))
                .collect(Collectors.toList());
    }

    public static List<Meeting> sortMeetings(List<Meeting> meetings) {
        return meetings.stream()
                .sorted(Comparator.comparing(Meeting::getMeetingStart).reversed())
                .collect(Collectors.toList());
    }

    public static List<Project> sortProjects(List<Project> projects) {
        return projects.stream()
                .sorted(Comparator.comparing(Project::getStatus).thenComparing(Project::getName))
                .collect(Collectors.toList());
    }

    public static List<Transaction> sortTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

}
