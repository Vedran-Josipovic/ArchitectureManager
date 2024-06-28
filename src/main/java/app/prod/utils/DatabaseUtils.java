package app.prod.utils;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.io.*;
import java.sql.*;

import app.prod.enumeration.Status;
import app.prod.enumeration.TransactionType;
import app.prod.model.*;
import org.slf4j.*;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String DATABASE_FILE = "conf/database.properties";

    //Make private when it won't be used in Main
    public static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String databaseUrl = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(databaseUrl, username, password);
    }

    public static void saveLocation(Location newLocation, String selectedType){
        if("Address".equals(selectedType)){
            saveAddress((Address) newLocation);
        } else if("VirtualLocation".equals(selectedType)){
            saveVirtualLocation((VirtualLocation) newLocation);
        }
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

    public static void saveTransaction(Transaction transaction) {
        try (Connection connection = connectToDatabase()) {
            String insertTransactionSql = "INSERT INTO TRANSACTION (NAME, TRANSACTION_TYPE, AMOUNT, DESCRIPTION, DATE) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSql);
            preparedStatement.setString(1, transaction.getName());
            preparedStatement.setString(2, transaction.getTransactionType().getName());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setString(4, transaction.getDescription());
            preparedStatement.setDate(5, Date.valueOf(transaction.getDate()));
            preparedStatement.execute();
            logger.info("Transaction saved successfully.");
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while saving transaction to database!";
            logger.error(message, ex);
            System.out.println(message);
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
            transactions.add(transaction);
        }
        return transactions;
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
                queryParams.put(paramOrdinalNumber++, transactionFilter.getTransactionType().getName());
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
                    null,
                    null,
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
        } catch (SQLException | IOException e) {
            String message = "An error occurred while retrieving filtered projects from the database!";
            logger.error(message, e);
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









}
