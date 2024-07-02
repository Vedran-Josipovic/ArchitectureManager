package app.prod.utils;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;
import java.sql.*;

import app.prod.enumeration.Status;
import app.prod.enumeration.TransactionType;
import app.prod.exception.NoSuchTransactionTypeException;
import app.prod.model.*;
import org.slf4j.*;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String DATABASE_FILE = "conf/database.properties";

    public static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String databaseUrl = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(databaseUrl, username, password);
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("An error occurred while closing the connection!", e);
        }
    }

    public static void saveTransaction(Transaction transaction) {
        Connection connection = null;
        try {
            connection = connectToDatabase();
            String insertTransactionSql = "INSERT INTO TRANSACTION (NAME, TRANSACTION_TYPE, AMOUNT, DESCRIPTION, DATE, PROJECT_ID) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSql);
            preparedStatement.setString(1, transaction.getName());


            preparedStatement.setString(2, transaction.getTransactionType().getName());


            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setString(4, transaction.getDescription());
            preparedStatement.setDate(5, Date.valueOf(transaction.getDate()));
            preparedStatement.setLong(6, transaction.getProject().getId());

            preparedStatement.execute();
            logger.info("Transaction saved successfully.");
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while saving transaction to database!";
            logger.error(message, ex);
            System.out.println(message);
        }
        finally {
            closeConnection(connection);
        }

    }

    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM TRANSACTION";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            transactions = mapResultSetToTransactionList(resultSet);
        } catch (SQLException | IOException e) {
            String message = "An error occurred while connecting to the database!";
            logger.error(message, e);
            System.out.println(message);
        }
        return transactions;
    }

    private static List<Transaction> mapResultSetToTransactionList(ResultSet resultSet) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        while (resultSet.next()) {
            Transaction transaction = new Transaction();
            transaction.setId(resultSet.getLong("ID"));
            transaction.setName(resultSet.getString("NAME"));
            transaction.setTransactionType(TransactionType.valueOf(resultSet.getString("TRANSACTION_TYPE").toUpperCase()));
            transaction.setAmount(resultSet.getBigDecimal("AMOUNT"));
            transaction.setDescription(resultSet.getString("DESCRIPTION"));
            transaction.setDate(resultSet.getDate("DATE").toLocalDate());
            transaction.setProject(getProjectById(resultSet.getLong("PROJECT_ID")));

            transactions.add(transaction);
        }
        return transactions;
    }

    private static void verifyTransactionType(Transaction transaction) throws NoSuchTransactionTypeException {
        if (
                !Objects.equals(transaction.getTransactionType().getName(), "Income") &&
                !Objects.equals(transaction.getTransactionType().getName(), "Expense")
        ){
            throw new NoSuchTransactionTypeException("Transaction type does not exist! Defaulting to EXPENSE.");
        }
    }

    public static List<Transaction> getTransactionsByFilters(Transaction transactionFilter, BigDecimal minAmount, BigDecimal maxAmount) {
        List<Transaction> transactions = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        int paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            StringBuilder baseSqlQuery = new StringBuilder("SELECT * FROM TRANSACTION WHERE 1=1");

            if (Optional.ofNullable(transactionFilter.getName()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(NAME) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + transactionFilter.getName().toLowerCase() + "%");
            }

            if (Optional.ofNullable(transactionFilter.getTransactionType()).isPresent()) {
                baseSqlQuery.append(" AND TRANSACTION_TYPE = ?");
                try {
                    verifyTransactionType(transactionFilter);
                    queryParams.put(paramOrdinalNumber++, transactionFilter.getTransactionType().getName());
                } catch (NoSuchTransactionTypeException e) {
                    logger.error(e.getMessage());
                    queryParams.put(paramOrdinalNumber++, TransactionType.EXPENSE.getName() );
                }
            }

            if (Optional.ofNullable(transactionFilter.getDate()).isPresent()) {
                baseSqlQuery.append(" AND DATE = ?");
                queryParams.put(paramOrdinalNumber++, Date.valueOf(transactionFilter.getDate()));
            }

            if (minAmount != null && maxAmount != null) {
                baseSqlQuery.append(" AND AMOUNT BETWEEN ? AND ?");
                queryParams.put(paramOrdinalNumber++, minAmount);
                queryParams.put(paramOrdinalNumber++, maxAmount);
            } else if (minAmount != null) {
                baseSqlQuery.append(" AND AMOUNT >= ?");
                queryParams.put(paramOrdinalNumber++, minAmount);
            } else if (maxAmount != null) {
                baseSqlQuery.append(" AND AMOUNT <= ?");
                queryParams.put(paramOrdinalNumber++, maxAmount);
            }

            if(Optional.ofNullable(transactionFilter.getProject()).isPresent()){
                baseSqlQuery.append(" AND PROJECT_ID = ?");
                queryParams.put(paramOrdinalNumber++, transactionFilter.getProject().getId());
            }


            PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.toString());
            logger.info(preparedStatement.toString());

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String stringQueryParam) {
                    preparedStatement.setString(paramNumber, stringQueryParam);
                } else if (queryParams.get(paramNumber) instanceof Long longQueryParam) {
                    preparedStatement.setLong(paramNumber, longQueryParam);
                } else if (queryParams.get(paramNumber) instanceof Date dateQueryParam) {
                    preparedStatement.setDate(paramNumber, dateQueryParam);
                } else if (queryParams.get(paramNumber) instanceof BigDecimal bigDecimal) {
                    preparedStatement.setBigDecimal(paramNumber, bigDecimal);
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            logger.info(resultSet.toString());
            transactions = mapResultSetToTransactionList(resultSet);
            transactions.forEach(t -> logger.info(t.toString()));
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while retrieving filtered transactions from the database!";
            logger.error(message, ex);
        }
        return transactions;
    }

    public static List<Client> getClientsByFilters(Client clientFilter) {
        List<Client> clients = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        int paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            StringBuilder baseSqlQuery = new StringBuilder("SELECT * FROM CLIENT WHERE 1=1");

            if (Optional.ofNullable(clientFilter.getId()).isPresent()) {
                baseSqlQuery.append(" AND ID = ?");
                queryParams.put(paramOrdinalNumber++, clientFilter.getId());
            }

            if (Optional.ofNullable(clientFilter.getName()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(NAME) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + clientFilter.getName().toLowerCase() + "%");
            }

            if (Optional.ofNullable(clientFilter.getEmail()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(EMAIL) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + clientFilter.getEmail().toLowerCase() + "%");
            }

            if (Optional.ofNullable(clientFilter.getCompanyName()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(COMPANY_NAME) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + clientFilter.getCompanyName().toLowerCase() + "%");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.toString());
            logger.info(preparedStatement.toString());

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String stringQueryParam) {
                    preparedStatement.setString(paramNumber, stringQueryParam);
                } else if (queryParams.get(paramNumber) instanceof Long longQueryParam) {
                    preparedStatement.setLong(paramNumber, longQueryParam);
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            logger.info(resultSet.toString());
            clients = mapResultSetToClientList(resultSet);
            clients.forEach(c -> logger.info(c.toString()));
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while retrieving filtered clients from the database!";
            logger.error(message, ex);
        }
        return clients;
    }

    private static List<Client> mapResultSetToClientList(ResultSet resultSet) throws SQLException {
        List<Client> clients = new ArrayList<>();
        while (resultSet.next()) {
            Client client = new Client();
            client.setId(resultSet.getLong("ID"));
            client.setName(resultSet.getString("NAME"));
            client.setEmail(resultSet.getString("EMAIL"));
            client.setCompanyName(resultSet.getString("COMPANY_NAME"));
            clients.add(client);
        }
        return clients;
    }

    public static void saveClient(Client client) {
        try (Connection connection = connectToDatabase()) {
            String insertClientSql = "INSERT INTO CLIENT (NAME, EMAIL, COMPANY_NAME) VALUES (?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertClientSql);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getEmail());
            preparedStatement.setString(3, client.getCompanyName());
            preparedStatement.execute();
            logger.info("Client saved successfully.");
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while saving client to database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static List<Client> getClients() {
        List<Client> clients = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM CLIENT";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            clients = mapResultSetToClientList(resultSet);
        } catch (SQLException | IOException e) {
            String message = "An error occurred while connecting to the database!";
            logger.error(message, e);
            System.out.println(message);
        }
        return clients;
    }

    private static List<Project> mapResultSetToProjectList(ResultSet resultSet) throws SQLException {
        List<Project> projects = new ArrayList<>();
        while (resultSet.next()) {
            Client client = getClientById(resultSet.getLong("CLIENT_ID"));
            Project project = new Project(
                    resultSet.getLong("ID"),
                    resultSet.getString("NAME"),
                    resultSet.getString("DESCRIPTION"),
                    resultSet.getDate("START_DATE").toLocalDate(),
                    resultSet.getDate("DEADLINE").toLocalDate(),
                    Status.valueOf(resultSet.getString("STATUS")),
                    client,
                    getTransactionsByProjectId(resultSet.getLong("ID")),
                    null
            );
            projects.add(project);
        }
        return projects;
    }

    public static List<Project> getProjectsByFilters(Project projectFilter) {
        List<Project> projects = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        int paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            StringBuilder baseSqlQuery = new StringBuilder("SELECT * FROM PROJECT WHERE 1=1");

            if (Optional.ofNullable(projectFilter.getName()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(NAME) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + projectFilter.getName().toLowerCase() + "%");
            }

            if (Optional.ofNullable(projectFilter.getDescription()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(DESCRIPTION) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + projectFilter.getDescription().toLowerCase() + "%");
            }

            if (Optional.ofNullable(projectFilter.getStartDate()).isPresent()) {
                baseSqlQuery.append(" AND START_DATE >= ?");
                queryParams.put(paramOrdinalNumber++, Date.valueOf(projectFilter.getStartDate()));
            }

            if (Optional.ofNullable(projectFilter.getDeadline()).isPresent()) {
                baseSqlQuery.append(" AND DEADLINE <= ?");
                queryParams.put(paramOrdinalNumber++, Date.valueOf(projectFilter.getDeadline()));
            }

            if (Optional.ofNullable(projectFilter.getStatus()).isPresent()) {
                baseSqlQuery.append(" AND STATUS = ?");
                queryParams.put(paramOrdinalNumber++, projectFilter.getStatus().name());
            }

            if (Optional.ofNullable(projectFilter.getClient()).isPresent()) {
                baseSqlQuery.append(" AND CLIENT_ID = ?");
                queryParams.put(paramOrdinalNumber++, projectFilter.getClient().getId());
            }

            PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.toString());
            for (Map.Entry<Integer, Object> entry : queryParams.entrySet()) {
                preparedStatement.setObject(entry.getKey(), entry.getValue());
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            projects = mapResultSetToProjectList(resultSet);
            projects.forEach(p -> logger.info(p.displayProject()));

        } catch (SQLException | IOException e) {
            String message = "An error occurred while retrieving filtered projects from the database!";
            logger.error(message, e);
        }

        return projects;
    }

    public static List<Project> getProjectsByClientId(Long clientId) {
        List<Project> projects = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM PROJECT WHERE CLIENT_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            projects = mapResultSetToProjectList(resultSet);
        } catch (SQLException | IOException e) {
            String message = "An error occurred while connecting to the database!";
            logger.error(message, e);
            System.out.println(message);
        }
        return projects;
    }

    private static Client getClientById(Long clientId) {
        Client client = null;
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM CLIENT WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                client = new Client(
                        resultSet.getLong("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("EMAIL"),
                        resultSet.getString("COMPANY_NAME")
                );
            }
        } catch (SQLException | IOException e) {
            logger.error("An error occurred while retrieving client by id from the database!", e);
        }
        return client;
    }

    public static void saveProject(Project project) {
        try (Connection connection = connectToDatabase()) {
            String insertProjectSql = "INSERT INTO PROJECT (NAME, DESCRIPTION, START_DATE, DEADLINE, STATUS, CLIENT_ID) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertProjectSql);
            preparedStatement.setString(1, project.getName());
            preparedStatement.setString(2, project.getDescription());
            preparedStatement.setDate(3, Date.valueOf(project.getStartDate()));
            preparedStatement.setDate(4, Date.valueOf(project.getDeadline()));
            preparedStatement.setString(5, project.getStatus().name());
            preparedStatement.setLong(6, project.getClient().getId());
            preparedStatement.execute();
            logger.info("Project saved successfully.");
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while saving project to database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static List<Project> getProjects() {
        List<Project> projects = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM PROJECT";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            projects = mapResultSetToProjectList(resultSet);
        } catch (SQLException | IOException e) {
            String message = "An error occurred while connecting to the database!";
            logger.error(message, e);
            System.out.println(message);
        }
        return projects;
    }

    public static Project getProjectById(Long projectId) {
        Project project = null;
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM PROJECT WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Client client = getClientById(resultSet.getLong("CLIENT_ID"));
                project = new Project(
                        resultSet.getLong("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("DESCRIPTION"),
                        resultSet.getDate("START_DATE").toLocalDate(),
                        resultSet.getDate("DEADLINE").toLocalDate(),
                        Status.valueOf(resultSet.getString("STATUS")),
                        client,
                        null,
                        null
                );
            }
        } catch (SQLException | IOException e) {
            logger.error("An error occurred while retrieving project by id from the database!", e);
        }
        return project;
    }

    private static Set<Transaction> getTransactionsByProjectId(Long projectId) {
        Set<Transaction> transactions = new HashSet<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM TRANSACTION WHERE PROJECT_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            transactions.addAll(mapResultSetToTransactionList(resultSet));
        } catch (SQLException | IOException e) {
            logger.error("An error occurred while retrieving transactions by project id from the database!", e);
        }
        return transactions;
    }


    public static void saveLocation(Location newLocation, String selectedType){
        if("Address".equals(selectedType)){
            saveAddress((Address) newLocation);
        } else if("VirtualLocation".equals(selectedType)){
            saveVirtualLocation((VirtualLocation) newLocation);
        }
    }

    public static List<Location> getLocations(){
        List<Location> locations = new ArrayList<>();
        try(Connection connection = connectToDatabase()){
            String sqlQuery = "SELECT * FROM LOCATION";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while(resultSet.next()){
                Location location = null;
                if("Address".equals(resultSet.getString("TYPE"))){
                    location = new Address(
                            resultSet.getLong("ID"),
                            resultSet.getString("STREET"),
                            resultSet.getString("HOUSE_NUMBER"),
                            resultSet.getString("CITY")
                    );
                } else if("VirtualLocation".equals(resultSet.getString("TYPE"))){
                    location = new VirtualLocation(
                            resultSet.getLong("ID"),
                            resultSet.getString("MEETING_LINK"),
                            resultSet.getString("PLATFORM")
                    );
                }
                locations.add(location);
            }
        } catch (SQLException | IOException e) {
            logger.error("An error occurred while retrieving locations from the database!", e);
        }
        return locations;
    }

    private static void saveAddress(Address address){
        try(Connection connection = connectToDatabase()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LOCATION (TYPE, STREET, HOUSE_NUMBER, CITY) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, "Address");
            preparedStatement.setString(2, address.getStreet());
            preparedStatement.setString(3, address.getHouseNumber());
            preparedStatement.setString(4, address.getCity());
            preparedStatement.execute();
            logger.info("Address saved successfully");
        } catch (SQLException | IOException e) {
            logger.error("Error while saving address: " + e.getMessage());
        }
    }

    private static void saveVirtualLocation(VirtualLocation virtualLocation){
        try(Connection connection = connectToDatabase()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LOCATION (TYPE, MEETING_LINK, PLATFORM) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "VirtualLocation");
            preparedStatement.setString(2, virtualLocation.getMeetingLink());
            preparedStatement.setString(3, virtualLocation.getPlatform());
            preparedStatement.execute();
            logger.info("Virtual location saved successfully");
        } catch (SQLException | IOException e) {
            logger.error("Error while saving virtual location", e);
        }
    }


    public static List<TransactionRecord<TransactionType, BigDecimal>> getTransactionData() {
        List<TransactionRecord<TransactionType, BigDecimal>> transactionDataList = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT TRANSACTION_TYPE, AMOUNT FROM TRANSACTION";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                TransactionType transactionType = TransactionType.valueOf(resultSet.getString("TRANSACTION_TYPE").toUpperCase());
                BigDecimal amount = resultSet.getBigDecimal("AMOUNT");
                transactionDataList.add(new TransactionRecord<>(transactionType, amount));
            }
        } catch (SQLException | IOException e) {
            String message = "An error occurred while connecting to the database!";
            logger.error(message, e);
            System.out.println(message);
        }
        return transactionDataList;
    }

    public static void saveEmployee(Employee employee) {
        try (Connection connection = connectToDatabase()) {
            String insertEmployeeSql = "INSERT INTO EMPLOYEE (NAME, EMAIL, POSITION, PROJECT_ID) VALUES (?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSql);
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getEmail());
            preparedStatement.setString(3, employee.getPosition());
            preparedStatement.setLong(4, employee.getProject().getId());

            preparedStatement.execute();
            logger.info("Employee saved successfully.");
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while saving employee to database!";
            logger.error(message, ex);
        }
    }

    public static List<Employee> getEmployeesByProjectId(Long projectId) {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM EMPLOYEE WHERE PROJECT_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getLong("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("EMAIL"),
                        resultSet.getString("POSITION"),
                        getProjectById(resultSet.getLong("PROJECT_ID"))
                );
                employees.add(employee);
            }
        } catch (SQLException | IOException e) {
            logger.error("An error occurred while retrieving employees by project id from the database!", e);
        }
        return employees;
    }

    public static List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM EMPLOYEE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getLong("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("EMAIL"),
                        resultSet.getString("POSITION"),
                        getProjectById(resultSet.getLong("PROJECT_ID"))
                );
                employees.add(employee);
            }
        } catch (SQLException | IOException e) {
            logger.error("An error occurred while retrieving employees from the database!", e);
        }
        return employees;
    }

    public static List<Employee> getEmployeesByFilters(Employee employeeFilter) {
        List<Employee> employees = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        int paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            StringBuilder baseSqlQuery = new StringBuilder("SELECT * FROM EMPLOYEE WHERE 1=1");

            if (Optional.ofNullable(employeeFilter.getId()).isPresent()) {
                baseSqlQuery.append(" AND ID = ?");
                queryParams.put(paramOrdinalNumber++, employeeFilter.getId());
            }

            if (Optional.ofNullable(employeeFilter.getName()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(NAME) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + employeeFilter.getName().toLowerCase() + "%");
            }

            if (Optional.ofNullable(employeeFilter.getEmail()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(EMAIL) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + employeeFilter.getEmail().toLowerCase() + "%");
            }

            if (Optional.ofNullable(employeeFilter.getPosition()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(POSITION) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + employeeFilter.getPosition().toLowerCase() + "%");
            }

            if (Optional.ofNullable(employeeFilter.getProject()).isPresent()) {
                baseSqlQuery.append(" AND PROJECT_ID = ?");
                queryParams.put(paramOrdinalNumber++, employeeFilter.getProject().getId());
            }

            PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.toString());
            logger.info(preparedStatement.toString());

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String stringQueryParam) {
                    preparedStatement.setString(paramNumber, stringQueryParam);
                } else if (queryParams.get(paramNumber) instanceof Long longQueryParam) {
                    preparedStatement.setLong(paramNumber, longQueryParam);
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            logger.info(resultSet.toString());
            employees = mapResultSetToEmployeeList(resultSet);
            employees.forEach(e -> logger.info(e.toString()));
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while retrieving filtered employees from the database!";
            logger.error(message, ex);
        }
        return employees;
    }

    private static List<Employee> mapResultSetToEmployeeList(ResultSet resultSet) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        while (resultSet.next()) {
            Employee employee = new Employee(
                    resultSet.getLong("ID"),
                    resultSet.getString("NAME"),
                    resultSet.getString("EMAIL"),
                    resultSet.getString("POSITION"),
                    getProjectById(resultSet.getLong("PROJECT_ID"))
            );
            employees.add(employee);
        }
        return employees;
    }





    public static void saveMeeting(Meeting meeting) {
        try (Connection connection = connectToDatabase()) {
            String insertMeetingSql = "INSERT INTO MEETING (NAME, MEETING_START, MEETING_END, LOCATION_ID, NOTES) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertMeetingSql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, meeting.getName());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(meeting.getMeetingStart()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(meeting.getMeetingEnd()));

            if (meeting.getLocation() instanceof Address) {
                Address address = (Address) meeting.getLocation();
                preparedStatement.setLong(4, address.getId());
            } else if (meeting.getLocation() instanceof VirtualLocation) {
                VirtualLocation virtualLocation = (VirtualLocation) meeting.getLocation();
                preparedStatement.setLong(4, virtualLocation.getId());
            }

            preparedStatement.setString(5, meeting.getNotes());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long meetingId = generatedKeys.getLong(1);
                saveParticipants(meetingId, meeting.getParticipants());
            }

            logger.info("Meeting saved successfully.");
        } catch (SQLException | IOException ex) {
            logger.error("An error occurred while saving the meeting to the database!", ex);
        }
    }

    private static void saveParticipants(long meetingId, Set<Contact> participants) {
        try(Connection connection = connectToDatabase()) {
            String insertParticipantSql = "INSERT INTO PARTICIPANT (MEETING_ID, PARTICIPANT_ID, PARTICIPANT_TYPE) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertParticipantSql);
            for (Contact participant : participants) {
                preparedStatement.setLong(1, meetingId);
                preparedStatement.setLong(2, participant.getId());
                preparedStatement.setString(3, participant instanceof Client ? "CLIENT" : "EMPLOYEE");
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            logger.info("Participants saved successfully.");
        } catch (SQLException | IOException ex) {
            logger.error("An error occurred while saving participants to the database!", ex);
        }
    }


    public static List<Meeting> getMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM MEETING";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                Meeting meeting = mapResultSetToMeeting(resultSet);
                meeting.setParticipants(getParticipantsByMeetingId(connection, meeting.getId()));
                meetings.add(meeting);
            }
        } catch (SQLException | IOException e) {
            logger.error("An error occurred while retrieving meetings from the database!", e);
        }
        return meetings;
    }


    private static Meeting mapResultSetToMeeting(ResultSet resultSet) throws SQLException, IOException {
        long id = resultSet.getLong("ID");
        String name = resultSet.getString("NAME");
        LocalDateTime meetingStart = resultSet.getTimestamp("MEETING_START").toLocalDateTime();
        LocalDateTime meetingEnd = resultSet.getTimestamp("MEETING_END").toLocalDateTime();
        Location location = getLocationById(resultSet.getLong("LOCATION_ID"));
        String notes = resultSet.getString("NOTES");
        return new Meeting(id, name, meetingStart, meetingEnd, location, new HashSet<>(), notes);
    }

    private static Location getLocationById(long locationId) throws SQLException, IOException {
        Location location = null;
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM LOCATION WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, locationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String type = resultSet.getString("TYPE");
                if ("Address".equals(type)) {
                    location = new Address(
                            resultSet.getLong("ID"),
                            resultSet.getString("STREET"),
                            resultSet.getString("HOUSE_NUMBER"),
                            resultSet.getString("CITY")
                    );
                } else if ("VirtualLocation".equals(type)) {
                    location = new VirtualLocation(
                            resultSet.getLong("ID"),
                            resultSet.getString("MEETING_LINK"),
                            resultSet.getString("PLATFORM")
                    );
                }
            }
        }
        return location;
    }

    private static Set<Contact> getParticipantsByMeetingId(Connection connection, long meetingId) {
        Set<Contact> participants = new HashSet<>();
        try {
            String sqlQuery = "SELECT * FROM PARTICIPANT WHERE MEETING_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, meetingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long participantId = resultSet.getLong("PARTICIPANT_ID");
                String participantType = resultSet.getString("PARTICIPANT_TYPE");
                if ("CLIENT".equals(participantType)) {
                    participants.add(getClientById(participantId));
                } else if ("EMPLOYEE".equals(participantType)) {
                    participants.add(getEmployeeById(participantId));
                }
            }
        } catch (SQLException | IOException ex) {
            logger.error("An error occurred while retrieving participants by meeting ID!", ex);
        }
        return participants;
    }

    private static Employee getEmployeeById(long employeeId) throws SQLException, IOException {
        Employee employee = null;
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM EMPLOYEE WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employee = new Employee(
                        resultSet.getLong("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("EMAIL"),
                        resultSet.getString("POSITION"),
                        getProjectById(resultSet.getLong("PROJECT_ID"))
                );
            }
        }
        return employee;
    }

    public static List<Meeting> getMeetingsByFilters(Meeting meetingFilter) {
        List<Meeting> meetings = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        int paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            StringBuilder baseSqlQuery = new StringBuilder("SELECT * FROM MEETING WHERE 1=1");

            if (Optional.ofNullable(meetingFilter.getName()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(NAME) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + meetingFilter.getName().toLowerCase() + "%");
            }

            if (Optional.ofNullable(meetingFilter.getMeetingStart()).isPresent()) {
                baseSqlQuery.append(" AND MEETING_START >= ?");
                queryParams.put(paramOrdinalNumber++, Timestamp.valueOf(meetingFilter.getMeetingStart()));
            }

            if (Optional.ofNullable(meetingFilter.getMeetingEnd()).isPresent()) {
                baseSqlQuery.append(" AND MEETING_END <= ?");
                queryParams.put(paramOrdinalNumber++, Timestamp.valueOf(meetingFilter.getMeetingEnd()));
            }

            if (Optional.ofNullable(meetingFilter.getLocation()).isPresent()) {
                baseSqlQuery.append(" AND LOCATION_ID = ?");
                if(meetingFilter.getLocation() instanceof Address address){
                    queryParams.put(paramOrdinalNumber++, address.getId());
                } else if(meetingFilter.getLocation() instanceof VirtualLocation virtualLocation){
                    queryParams.put(paramOrdinalNumber++, virtualLocation.getId());
                }

            }

            PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.toString());
            logger.info(preparedStatement.toString());

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String stringQueryParam) {
                    preparedStatement.setString(paramNumber, stringQueryParam);
                } else if (queryParams.get(paramNumber) instanceof Long longQueryParam) {
                    preparedStatement.setLong(paramNumber, longQueryParam);
                } else if (queryParams.get(paramNumber) instanceof Timestamp timestampQueryParam) {
                    preparedStatement.setTimestamp(paramNumber, timestampQueryParam);
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            logger.info(resultSet.toString());
            while (resultSet.next()) {
                Meeting meeting = mapResultSetToMeeting(resultSet);
                meeting.setParticipants(getParticipantsByMeetingId(connection, meeting.getId()));
                meetings.add(meeting);
            }
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while retrieving filtered meetings from the database!";
            logger.error(message, ex);
        }
        return meetings;
    }

    public static List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.addAll(getClients());
        contacts.addAll(getEmployees());
        return contacts;
    }






}
